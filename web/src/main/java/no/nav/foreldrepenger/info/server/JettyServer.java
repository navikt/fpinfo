package no.nav.foreldrepenger.info.server;

import static no.nav.foreldrepenger.konfig.Cluster.NAIS_CLUSTER_NAME;
import static org.eclipse.jetty.webapp.MetaInfConfiguration.WEBINF_JAR_PATTERN;

import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.app.konfig.ApplicationConfig;
import no.nav.foreldrepenger.info.app.konfig.InternalApplication;
import no.nav.foreldrepenger.info.dbstoette.DBConnectionProperties;
import no.nav.foreldrepenger.info.dbstoette.DatabaseStøtte;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.security.token.support.core.configuration.IssuerProperties;
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration;
import no.nav.security.token.support.jaxrs.servlet.JaxrsJwtTokenValidationFilter;

public class JettyServer {

    public static final String ACR_LEVEL4 = "acr=Level4";
    public static final String TOKENX = "tokenx";

    private static final Environment ENV = Environment.current();

    private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);

    static {
        System.setProperty(NAIS_CLUSTER_NAME, ENV.clusterName());
    }

    private static final String CONTEXT_PATH = "/fpinfo";
    private final int hostPort;

    public JettyServer(int hostPort) {
        this.hostPort = hostPort;
    }

    public static void main(String[] args) throws Exception {
        var jettyServer = new JettyServer(8080);
        jettyServer.migrerDatabaseScript();
        jettyServer.start();
    }

    private static List<DBConnectionProperties> getDBConnectionProperties() {
        try (var in = JettyServer.class.getResourceAsStream("/jetty_web_server.json");) {
            var props = DBConnectionProperties.fraStream(in);
            LOG.info("DB connection properties {}", props);
            return props;
        } catch (Exception e) {
            LOG.info("DB connection properties feilet", e);
            throw new RuntimeException(e);
        }
    }

    protected void start() throws Exception {
        var server = new Server();
        var connector = new ServerConnector(server);
        connector.setPort(getServerPort());
        server.addConnector(connector);
        var ctx = createContext();
        server.setHandler(ctx);

        server.start();
        server.join();
    }

    protected int getServerPort() {
        return hostPort;
    }

    protected void migrerDatabaseScript() {
        try {
            var dbs = getDBConnectionProperties();
            DatabaseStøtte.settOppJndiForDefaultDataSource(dbs);
            DatabaseStøtte.kjørMigreringFor(dbs);
        } catch (Exception e) {
            LOG.error("Feil under migrering", e);
            throw e;
        }

    }

    protected WebAppContext createContext() {
        var ctx = new WebAppContext();
        ctx.setParentLoaderPriority(true);
        ctx.setBaseResource(createResourceCollection());
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        ctx.setContextPath(CONTEXT_PATH);
        ctx.setAttribute(WEBINF_JAR_PATTERN, "^.*jersey-.*.jar$|^.*felles-.*.jar$");
        ctx.addEventListener(new org.jboss.weld.environment.servlet.BeanManagerResourceBindingListener());
        ctx.addEventListener(new org.jboss.weld.environment.servlet.Listener());
        updateMetaData(ctx.getMetaData());
        addFilters(ctx);
        return ctx;
    }

    protected void addFilters(WebAppContext ctx) {
        var dispatcherType = EnumSet.of(DispatcherType.REQUEST);

        LOG.trace("Installerer JaxrsJwtTokenValidationFilter");
        ctx.addFilter(new FilterHolder(new JaxrsJwtTokenValidationFilter(config())),
                "/api/*",
                dispatcherType);
        ctx.addFilter(new FilterHolder(new HeadersToMDCFilterBean()),
                "/api/*",
                dispatcherType);
        var corsFilter = ctx.addFilter(CrossOriginFilter.class,
                "/*",
                dispatcherType);
        corsFilter.setInitParameter("allowedOrigins", "*");
    }

    private static MultiIssuerConfiguration config() {
        return new MultiIssuerConfiguration(
                Map.of(TOKENX, issuerProperties("token.x.well.known.url", "token.x.client.id")));
    }

    private static IssuerProperties issuerProperties(String wellKnownUrl, String clientId) {
        return new IssuerProperties(ENV.getRequiredProperty(wellKnownUrl, URL.class), List.of(ENV.getRequiredProperty(clientId)));
    }

    private void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.

        metaData.setWebInfClassesResources(getApplicationClasses().stream()
                .map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation()))
                .distinct()
                .toList());
    }

    protected List<Class<?>> getApplicationClasses() {
        return List.of(ApplicationConfig.class, InternalApplication.class);
    }

    protected Resource createResourceCollection() {
        return new ResourceCollection(
                Resource.newClassPathResource("META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web"));
    }
}
