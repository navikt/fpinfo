package no.nav.foreldrepenger.info.server;

import static no.nav.foreldrepenger.konfig.Cluster.NAIS_CLUSTER_NAME;
import static org.eclipse.jetty.webapp.MetaInfConfiguration.WEBINF_JAR_PATTERN;

import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.DispatcherType;
import javax.sql.DataSource;

import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.jaspi.DefaultAuthConfigFactory;
import org.eclipse.jetty.security.jaspi.JaspiAuthenticatorFactory;
import org.eclipse.jetty.security.jaspi.provider.JaspiAuthConfigProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.app.konfig.ApplicationConfig;
import no.nav.foreldrepenger.info.app.konfig.InternalApplication;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.security.token.support.core.configuration.IssuerProperties;
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration;
import no.nav.security.token.support.jaxrs.servlet.JaxrsJwtTokenValidationFilter;
import no.nav.vedtak.sikkerhet.jaspic.OidcAuthModule;

public class JettyServer {

    public static final String ACR_LEVEL4 = "acr=Level4";
    public static final String TOKENX = "tokenx";
    private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);
    private static final Environment ENV = Environment.current();
    private static final String CONTEXT_PATH = ENV.getProperty("context.path","/fpinfo");

    static {
        System.setProperty(NAIS_CLUSTER_NAME, ENV.clusterName());
    }

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
        konfigurerSikkerhet();
        // Vi migrerer ikke for defaultDS siden den ikke har noe migreringer uansett. Brukeren skal benytte fpinfo_schema skjema.
        settJdniOppslag(DatasourceUtil.createDatasource("defaultDS", 30));

        migrerDatabase();

        start();
    }

    private static void konfigurerSikkerhet() {

        var factory = new DefaultAuthConfigFactory();
        factory.registerConfigProvider(new JaspiAuthConfigProvider(new OidcAuthModule()),
                "HttpServlet",
                "server " + CONTEXT_PATH,
                "OIDC Authentication");

        AuthConfigFactory.setFactory(factory);
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
        var ctx = createContext();
        server.setHandler(ctx);

        server.start();
        server.join();
        LOG.info("Jetty startet on port: " + getServerPort());
    }

    private WebAppContext createContext() {
        var ctx = new WebAppContext();
        ctx.setParentLoaderPriority(true);
        ctx.setBaseResource(createResourceCollection());
        ctx.setContextPath(CONTEXT_PATH);
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        ctx.setAttribute(WEBINF_JAR_PATTERN, "^.*jersey-.*.jar$|^.*felles-.*.jar$");
        ctx.addEventListener(new org.jboss.weld.environment.servlet.BeanManagerResourceBindingListener());
        ctx.addEventListener(new org.jboss.weld.environment.servlet.Listener());
        ctx.setSecurityHandler(createSecurityHandler());
        updateMetaData(ctx.getMetaData());
        ctx.setThrowUnavailableOnStartupException(true);
        addFilters(ctx);
        //addFiltersTokenSupport(ctx);
        return ctx;
    }

    private static Resource createResourceCollection() {
        return new ResourceCollection(
                Resource.newClassPathResource("/META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web"));
    }

    private void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.
        metaData.setWebInfClassesResources(getWebInfClasses().stream()
                .map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation()))
                .distinct()
                .toList());
    }

    protected List<Class<?>> getWebInfClasses() {
        return List.of(ApplicationConfig.class, InternalApplication.class);
    }

    private static void addFilters(WebAppContext ctx) {
        var dispatcherType = EnumSet.of(DispatcherType.REQUEST);

        var corsFilter = ctx.addFilter(CrossOriginFilter.class,
                "/*",
                dispatcherType);
        corsFilter.setInitParameter("allowedOrigins", "*");
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

    private static SecurityHandler createSecurityHandler() {
        var securityHandler = new ConstraintSecurityHandler();
        securityHandler.setAuthenticatorFactory(new JaspiAuthenticatorFactory());
        var loginService = new JAASLoginService();
        loginService.setName("jetty-login");
        loginService.setLoginModuleName("jetty-login");
        loginService.setIdentityService(new DefaultIdentityService());
        securityHandler.setLoginService(loginService);
        return securityHandler;
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
}
