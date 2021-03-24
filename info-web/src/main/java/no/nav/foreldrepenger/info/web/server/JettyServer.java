package no.nav.foreldrepenger.info.web.server;

import static javax.servlet.DispatcherType.REQUEST;
import static no.nav.vedtak.util.env.Cluster.NAIS_CLUSTER_NAME;
import static org.eclipse.jetty.webapp.MetaInfConfiguration.WEBINF_JAR_PATTERN;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.dbstoette.DBConnectionProperties;
import no.nav.foreldrepenger.info.dbstoette.DatabaseStøtte;
import no.nav.foreldrepenger.info.web.app.konfig.ApplicationConfig;
import no.nav.foreldrepenger.info.web.app.konfig.InternalApplication;
import no.nav.security.token.support.core.configuration.IssuerProperties;
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration;
import no.nav.vedtak.util.env.Environment;

public class JettyServer {

    private static final Environment ENV = Environment.current();

    private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);

    static {
        System.setProperty(NAIS_CLUSTER_NAME, ENV.clusterName());
    }

    /**
     * nedstrippet sett med Jetty configurations for raskere startup.
     */
    private static final Configuration[] CONFIGURATIONS = new Configuration[] {
            new WebXmlConfiguration(),
            new AnnotationConfiguration(),
            new WebAppConfiguration(),
            new WebInfConfiguration(),
            new PlusConfiguration(),
            new EnvConfiguration(),
    };

    private static final String CONTEXT_PATH = "/fpinfo";
    private static final String SERVER_HOST = "0.0.0.0";
    private int hostPort;

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
            var props = DBConnectionProperties.fraStream1(in);
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
        connector.setHost(SERVER_HOST);
        server.addConnector(connector);
        var ctx = createContext();
        server.setHandler(ctx);
        server.start();
        server.join();
    }

    private void addTokenValidationFilter(WebAppContext ctx) {
        LOG.trace("Installerer Logging filter");
        ctx.addFilter(new FilterHolder(new LoggingFilter(config())),
                "/api/*",
                EnumSet.of(REQUEST));
    }

    private static MultiIssuerConfiguration config() {
        var config = new MultiIssuerConfiguration(
                Map.of(
                        "tokenx", issuerProperties("token.x.well.known.url", "token.x.client.id"),
                        "selvbetjening", issuerProperties("loginservice.idporten.discovery.url", "loginservice.idporten.audience")));
        LOG.trace("Konfig {}", config);
        return config;
    }

    private static IssuerProperties issuerProperties(String wellKnownUrl, String clientId) {
        var props = new IssuerProperties(ENV.getRequiredProperty(wellKnownUrl, URL.class), List.of(ENV.getRequiredProperty(clientId)));
        LOG.trace("Properties {}, {}", props.getDiscoveryUrl(), props.getAcceptedAudience());
        return props;
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

    protected WebAppContext createContext() throws MalformedURLException {
        var ctx = new WebAppContext();
        ctx.setParentLoaderPriority(true);
        // må hoppe litt bukk for å hente web.xml fra classpath i stedet for fra
        // filsystem.
        String descriptor;
        try (var resource = Resource.newClassPathResource("/WEB-INF/web.xml")) {
            descriptor = resource.getURI().toURL().toExternalForm();
        }
        ctx.setDescriptor(descriptor);
        ctx.setBaseResource(createResourceCollection());
        ctx.setContextPath(CONTEXT_PATH);
        ctx.setConfigurations(CONFIGURATIONS);
        ctx.setAttribute(WEBINF_JAR_PATTERN, "^.*resteasy-.*.jar$|^.*felles-.*.jar$");
        updateMetaData(ctx.getMetaData());
        addTokenValidationFilter(ctx);
        return ctx;
    }

    private void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.
        metaData.setWebInfClassesResources(getApplicationClasses().stream()
                .map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation()))
                .collect(Collectors.toList()));
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
