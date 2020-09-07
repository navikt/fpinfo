package no.nav.foreldrepenger.info.web.server;

import static no.nav.vedtak.util.env.Cluster.NAIS_CLUSTER_NAME;

import java.io.InputStream;
import java.net.MalformedURLException;
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
import no.nav.vedtak.util.env.Environment;

public class JettyServer {

    private static final Environment ENV = Environment.current();

    private static final Logger LOG = LoggerFactory.getLogger(JettyServer.class);

    private static final String DB_SCHEMAS = "jetty_web_server.json";

    static {
        System.setProperty(NAIS_CLUSTER_NAME, ENV.clusterName());
    }

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
    private static final String SERVER_HOST = "0.0.0.0";
    private int hostPort;

    public JettyServer(int hostPort) {
        this.hostPort = hostPort;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Mangler port");
        }
        JettyServer jettyServer = new JettyServer(Integer.parseUnsignedInt(args[0]));
        jettyServer.migrerDatabaseScript();
        jettyServer.start();
    }

    private static List<DBConnectionProperties> getDBConnectionProperties() {
        InputStream in = JettyServer.class.getResourceAsStream("/" + DB_SCHEMAS);
        var props = DBConnectionProperties.fraStream(in);
        LOG.info("DB connection properties {}", props);
        try {
            InputStream in1 = JettyServer.class.getResourceAsStream("/jetty_web_server1.json");
            var props1 = DBConnectionProperties.fraStream1(in1);
            LOG.info("DB connection properties 1{}", props1);
            LOG.info("DB connection properties sammenlignet {}", props.equals(props1));
            return props1;

        } catch (Exception e) {
            LOG.info("DB connection properties feilet", e);
            throw new RuntimeException(e);
        }
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
        webAppContext.setAttribute("org.eclipse.jetty.server.webapp.WebInfIncludeJarPattern",
                "^.*resteasy-.*.jar$|^.*felles-.*.jar$");
        new JettySikkerhetKonfig().konfigurer(webAppContext);
        updateMetaData(webAppContext.getMetaData());
        return webAppContext;
    }

    private void updateMetaData(MetaData metaData) {
        // Find path to class-files while starting jetty from development environment.
        metaData.setWebInfClassesDirs(getApplicationClasses().stream()
                .map(c -> Resource.newResource(c.getProtectionDomain().getCodeSource().getLocation()))
                .collect(Collectors.toList()));
    }

    protected List<Class<?>> getApplicationClasses() {
        return List.of(ApplicationConfig.class, IssoApplication.class);
    }

    protected Resource createResourceCollection() {
        return new ResourceCollection(
                Resource.newClassPathResource("META-INF/resources/webjars/"),
                Resource.newClassPathResource("/web"));
    }

}
