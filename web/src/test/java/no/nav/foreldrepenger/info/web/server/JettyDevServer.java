package no.nav.foreldrepenger.info.web.server;

import static java.lang.System.getProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import no.nav.foreldrepenger.info.dbstoette.Databaseskjemainitialisering;
import no.nav.foreldrepenger.info.dbstoette.DatasourceConfiguration;

/**
 * Setter opp jetty automatisk lokalt med riktig konfig verdier.
 */

/**
 * Setter opp jetty automatisk lokalt med riktig konfig verdier.
 */
public class JettyDevServer extends JettyServer {

    /**
     * @see https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html
     */
    private static final String TRUSTSTORE_PASSW_PROP = "javax.net.ssl.trustStorePassword";
    private static final String TRUSTSTORE_PATH_PROP = "javax.net.ssl.trustStore";

    private static final String WEBAPP_ROOT = "./";
    private static final int DEV_SERVER_PORT = 8040;

    public JettyDevServer() {
        super(DEV_SERVER_PORT);
    }

    public static void main(String[] args) throws Exception {
        JettyDevServer jettyDevServer = new JettyDevServer();
        jettyDevServer.konfigurer();
        jettyDevServer.migrerDatabaseScript();
        jettyDevServer.start();
    }

    private static void setupSikkerhetLokalt() throws IOException {
        System.setProperty("app.confdir", "src/main/resources/jetty");
        initCryptoStoreConfig("truststore", TRUSTSTORE_PATH_PROP, TRUSTSTORE_PASSW_PROP, "changeit");

        // Eksponer truststore for run-java-local.sh
        File tempTrustStore = new File(getProperty("javax.net.ssl.trustStore"));
        File truststore = new File("./truststore.jts");
        Files.copy(tempTrustStore.toPath(), truststore.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static String initCryptoStoreConfig(String storeName, String storeProperty, String storePasswordProperty,
            String defaultPassword) {
        String defaultLocation = getProperty("user.home", ".") + "/.modig/" + storeName + ".jks";

        String storePath = getProperty(storeProperty, defaultLocation);
        File storeFile = new File(storePath);
        if (!storeFile.exists()) {
            throw new IllegalStateException("Finner ikke " + storeName + " i " + storePath
                    + "\n\tKonfigurer enten som System property \'" + storeProperty + "\' eller environment variabel \'"
                    + storeProperty.toUpperCase().replace('.', '_') + "\'");
        }
        String password = getProperty(storePasswordProperty, defaultPassword);
        if (password == null) {
            throw new IllegalStateException(
                    "Passord for å aksessere store " + storeName + " i " + storePath + " er null");
        }

        System.setProperty(storeProperty, storeFile.getAbsolutePath());
        System.setProperty(storePasswordProperty, password);
        return storePath;
    }

    protected void konfigurer() throws Exception {
        File webapproot = new File(WEBAPP_ROOT);
        if (!webapproot.exists()) {
            throw new IllegalStateException("Har du satt working dir til server prosjekt? Finner ikke " + webapproot);
        }
        setupSikkerhetLokalt();
    }

    @Override
    protected int getServerPort() {
        return DEV_SERVER_PORT;
    }

    @Override
    protected void migrerDatabaseScript() {
        Databaseskjemainitialisering.migrerSkjemaer(DatasourceConfiguration.JETTY_DEV_WEB_SERVER);
    }

}
