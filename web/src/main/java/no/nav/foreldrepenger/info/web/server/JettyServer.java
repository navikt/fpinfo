package no.nav.foreldrepenger.info.web.server;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.dbstoette.DBConnectionProperties;
import no.nav.foreldrepenger.info.dbstoette.DatabaseStøtte;
import no.nav.foreldrepenger.info.web.app.konfig.ApplicationConfig;
import no.nav.foreldrepenger.info.web.server.sikkerhet.JettySikkerhetKonfig;
import no.nav.vedtak.isso.IssoApplication;
import no.nav.vedtak.sikkerhetsfilter.SecurityFilter;

public class JettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);

    private static final String DB_SCHEMAS = "jetty_web_server.json";

    /**
     * nedstrippet sett med Jetty configurations for raskere startup.
     */
    private static final Configuration[] CONFIGURATIONS = new Configuration[] {
            new WebXmlConfiguration(),
            new AnnotationConfiguration(),
            new WebInfConfiguration(),
            new PlusConfiguration(),
            new EnvConfiguration(),
    };

    private static final String CONTEXT_PATH = "/fpinfo";
    private static final String SERVER_HOST = "0.0.0.0"; // NOSONAR
    private int hostPort;

    public JettyServer(int hostPort) {
        this.hostPort = hostPort;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Mangler port");
        }

        JettyServer jettyServer = new JettyServer(Integer.parseUnsignedInt(args[0]));

        jettyServer.konfigurer();
        jettyServer.migrerDatabaseScript();
        jettyServer.start();
    }

    public static List<DBConnectionProperties> getDBConnectionProperties() {
        InputStream in = JettyServer.class.getResourceAsStream("/" + DB_SCHEMAS);
        return DBConnectionProperties.fraStream(in);
    }

    protected void start() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(getServerPort());
        connector.setHost(SERVER_HOST);
        server.addConnector(connector);
        server.setHandler(createContext());
        server.start();
        server.join();
    }

    protected int getServerPort() {
        return hostPort;
    }

    protected void konfigurer() throws Exception { // NOSONAR
        hacksForManglendeStøttePåNAIS();
        konfigurerSwaggerHash();
    }

    /**
     * @see SecurityFilter#getSwaggerHash()
     */
    protected void konfigurerSwaggerHash() {
        System.setProperty(SecurityFilter.SWAGGER_HASH_KEY, "sha256-E2MuDn7TjNl6T+nRqM4DMP4tBI4qnU9dc7+hiOBbAMU=");
    }

    protected void migrerDatabaseScript() {
        DatabaseKonfigINaisEnvironment.setup();
        try {
            var dbPropertiesList = getDBConnectionProperties();
            DatabaseStøtte.settOppJndiForDefaultDataSource(dbPropertiesList);
            DatabaseStøtte.kjørMigreringFor(dbPropertiesList);
        } catch (Exception e) {
            LOGGER.error("Feil under migrering", e);
            throw e;
        }

    }

    protected WebAppContext createContext() throws MalformedURLException {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setParentLoaderPriority(true);

        // må hoppe litt bukk for å hente web.xml fra classpath i stedet for fra
        // filsystem.
        String descriptor;
        try (var resource = Resource.newClassPathResource("/WEB-INF/web.xml")) {
            descriptor = resource.getURI().toURL().toExternalForm();
        }
        webAppContext.setDescriptor(descriptor);
        webAppContext.setBaseResource(createResourceCollection());
        webAppContext.setContextPath(CONTEXT_PATH);
        webAppContext.setConfigurations(CONFIGURATIONS);
        webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                "^.*resteasy-.*.jar$|^.*felles-.*.jar$");
        new JettySikkerhetKonfig().konfigurer(webAppContext);
        updateMetaData(webAppContext.getMetaData());
        return webAppContext;
    }

    private void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.
        List<Class<?>> appClasses = getApplicationClasses();

        List<Resource> resources = appClasses.stream()
                .map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation()))
                .collect(Collectors.toList());

        metaData.setWebInfClassesDirs(resources);
    }

    protected List<Class<?>> getApplicationClasses() {
        return Arrays.asList(ApplicationConfig.class, IssoApplication.class);
    }

    protected Resource createResourceCollection() {
        return new ResourceCollection(
                Resource.newClassPathResource("META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web"));
    }

    private void hacksForManglendeStøttePåNAIS() {
        loadBalancerFqdnTilLoadBalancerUrl();
        temporært();
    }

    private void loadBalancerFqdnTilLoadBalancerUrl() {
        if (System.getenv("LOADBALANCER_FQDN") != null) {
            String loadbalancerFqdn = System.getenv("LOADBALANCER_FQDN");
            String protocol = (loadbalancerFqdn.startsWith("localhost")) ? "http" : "https";
            System.setProperty("loadbalancer.url", protocol + "://" + loadbalancerFqdn);
        }
    }

    private void temporært() {
        // FIXME (u139158): PFP-1176 Skriv om i OpenAmIssoHealthCheck og
        // AuthorizationRequestBuilder når Jboss dør
        if (System.getenv("OIDC_OPENAM_HOSTURL") != null) {
            System.setProperty("OpenIdConnect.issoHost", System.getenv("OIDC_OPENAM_HOSTURL"));
        }
        // FIXME (u139158): PFP-1176 Skriv om i AuthorizationRequestBuilder og
        // IdTokenAndRefreshTokenProvider når Jboss dør
        if (System.getenv("OIDC_OPENAM_AGENTNAME") != null) {
            System.setProperty("OpenIdConnect.username", System.getenv("OIDC_OPENAM_AGENTNAME"));
        }
        // FIXME (u139158): PFP-1176 Skriv om i IdTokenAndRefreshTokenProvider når Jboss
        // dør
        if (System.getenv("OIDC_OPENAM_PASSWORD") != null) {
            System.setProperty("OpenIdConnect.password", System.getenv("OIDC_OPENAM_PASSWORD"));
        }
        // FIXME (u139158): PFP-1176 Skriv om i BaseJmsKonfig når Jboss dør
        if (System.getenv("FPSAK_CHANNEL_NAME") != null) {
            System.setProperty("mqGateway02.channel", System.getenv("FPSAK_CHANNEL_NAME"));
        }
    }
}
