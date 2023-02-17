package no.nav.foreldrepenger.info.server;

import static org.eclipse.jetty.webapp.MetaInfConfiguration.CONTAINER_JAR_PATTERN;

import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import no.nav.foreldrepenger.konfig.Environment;
import no.nav.security.token.support.core.configuration.IssuerProperties;
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration;
import no.nav.security.token.support.jaxrs.servlet.JaxrsJwtTokenValidationFilter;

public class JettyServer {

    public static final String ACR_LEVEL4 = "acr=Level4";
    public static final String TOKENX = "tokenx";
    private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);
    private static final Environment ENV = Environment.current();
    private static final String CONTEXT_PATH = ENV.getProperty("context.path","/fpinfo");
    private static final String JETTY_SCAN_LOCATIONS = "^.*jersey-.*\\.jar$|^.*felles-.*\\.jar$|^.*/app\\.jar$";
    private static final String JETTY_LOCAL_CLASSES = "^.*/target/classes/|";

    private final int serverPort;

    public static void main(String[] args) throws Exception {
        jettyServer(args).bootStrap();
    }

    protected static JettyServer jettyServer(String[] args) {
        if (args.length > 0) {
            return new JettyServer(Integer.parseUnsignedInt(args[0]));
        }
        return new JettyServer(ENV.getProperty("server.port", Integer.class, 8080));
    }

    protected JettyServer(int serverPort) {
        this.serverPort = serverPort;
    }

    protected void bootStrap() throws Exception {
        // Vi migrerer ikke for defaultDS siden den ikke har noe migreringer uansett. Brukeren skal benytte fpinfo_schema skjema.
        settJdniOppslag(DatasourceUtil.createDatasource("defaultDS", 30));

        migrerDatabase();

        start();
    }

    private static void settJdniOppslag(DataSource dataSource) throws NamingException {
        new EnvEntry("jdbc/defaultDS", dataSource);
    }

    protected void migrerDatabase() {
        try (var dataSource = DatasourceUtil.createDatasource("fpinfoSchema", 4)) {
            var configuration = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:/db/migration/fpinfoSchema")
                    .table("schema_version")
                    .baselineOnMigrate(true);
            if (ENV.isLocal() || ENV.isVTP()) {
                configuration = configuration.cleanDisabled(false).cleanOnValidationError(true);
            }
            configuration
                    .load()
                    .migrate();
        } catch (FlywayException e) {
            LOG.error("Feil under migrering av databasen.", e);
            throw e;
        }
    }

    private void start() throws Exception {
        var server = new Server(getServerPort());
        var connector = new ServerConnector(server);
        server.addConnector(connector);
        var handlers = new HandlerList(new ResetLogContextHandler(), createContext());
        server.setHandler(handlers);
        server.start();
        server.join();
        LOG.info("Jetty startet on port: " + getServerPort());
    }

    private ContextHandler createContext() {
        var ctx = new WebAppContext();
        ctx.setParentLoaderPriority(true);
        ctx.setContextPath(CONTEXT_PATH);
        ctx.setResourceBase(".");
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        ctx.setAttribute(CONTAINER_JAR_PATTERN, String.format("%s%s", ENV.isLocal() ? JETTY_LOCAL_CLASSES : "", JETTY_SCAN_LOCATIONS));
        ctx.addEventListener(new org.jboss.weld.environment.servlet.Listener());
        ctx.addEventListener(new org.jboss.weld.environment.servlet.BeanManagerResourceBindingListener());

        ctx.setThrowUnavailableOnStartupException(true);
        addFiltersTokenSupport(ctx);
        return ctx;
    }

    private static void addFiltersTokenSupport(WebAppContext ctx) {
        var dispatcherType = EnumSet.of(DispatcherType.REQUEST);

        LOG.trace("Installerer JaxrsJwtTokenValidationFilter");
        ctx.addFilter(new FilterHolder(new JaxrsJwtTokenValidationFilter(config())),
                "/api/*",
                dispatcherType);
        ctx.addFilter(new FilterHolder(new HeadersToMDCFilterBean()),
                "/api/*",
                dispatcherType);
    }

    private static MultiIssuerConfiguration config() {
        return new MultiIssuerConfiguration(
                Map.of(TOKENX, issuerProperties("token.x.well.known.url", "token.x.client.id")));
    }

    private static IssuerProperties issuerProperties(String wellKnownUrl, String clientId) {
        return new IssuerProperties(ENV.getRequiredProperty(wellKnownUrl, URL.class), List.of(ENV.getRequiredProperty(clientId)));
    }

    private int getServerPort() {
        return serverPort;
    }

    /**
     * Legges først slik at alltid resetter context før prosesserer nye requests.
     * Kjøres først så ikke risikerer andre har satt Request#setHandled(true).
     */
    static final class ResetLogContextHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
            MDC.clear();
        }
    }
}
