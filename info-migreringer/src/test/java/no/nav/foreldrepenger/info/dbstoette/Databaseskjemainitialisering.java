package no.nav.foreldrepenger.info.dbstoette;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.Environment;

/**
 * Initielt skjemaoppsett + migrering av unittest-skjemaer
 */
public final class Databaseskjemainitialisering {

    private static final Logger LOG = LoggerFactory.getLogger(Databaseskjemainitialisering.class);
    private static final String TMP_DIR = "java.io.tmpdir";
    private static final String SEMAPHORE_FIL_PREFIX = "no.nav.vedtak.felles.behandlingsprosess";
    private static final String SEMAPHORE_FIL_SUFFIX = "no.nav.vedtak.felles.behandlingsprosess";
    private static final Environment ENV = Environment.current();
    private static final Pattern placeholderPattern = Pattern.compile("\\$\\{(.*)}");

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Oslo"));

        if (ENV.getProperty("skipTests") != null || ENV.getProperty("maven.test.skip") != null) {
            LOG.info(
                    "Maven-property 'skipTests' eller 'maven.test.skip' er satt. Hopper over migrering av unittest-skjema");
        } else {
            migrerUnittestSkjemaer();
        }
    }

    public static void migrerUnittestSkjemaer() {
        migrerSkjemaer(DatasourceConfiguration.UNIT_TEST);
    }

    public static void migrerSkjemaer(DatasourceConfiguration skjemaer) {

        try {
            settOppSkjemaer(skjemaer);
            LokalDatabaseStøtte.kjørMigreringFor(skjemaer.get());
        } catch (RuntimeException e) {
            LOG.warn(
                    "\n\n Kunne ikke starte inkrementell oppdatering av databasen. Det finnes trolig endringer i allerede kjørte script.\nKjører full migrering...");
            settOppSkjemaer(skjemaer);
            LokalDatabaseStøtte.kjørFullMigreringFor(skjemaer.get());
        }

        slettAlleSemaphorer();
        try {
            Files.createTempFile(SEMAPHORE_FIL_PREFIX, SEMAPHORE_FIL_SUFFIX);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void kjørMigreringHvisNødvendig() {
        if (!Databaseskjemainitialisering.erMigreringKjørt()) {
            LOG.info("Kjører migrering på nytt");
            Databaseskjemainitialisering.migrerUnittestSkjemaer();
        } else {

            try {
                settSchemaPlaceholder(DatasourceConfiguration.UNIT_TEST.getRaw());
                LokalDatabaseStøtte.settOppJndiForDefaultDataSource(DatasourceConfiguration.UNIT_TEST.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void settOppSkjemaer(DatasourceConfiguration skjemaer) {
        settSchemaPlaceholder(skjemaer.getRaw());
        LOG.info("Kjører migrering for {}", DatasourceConfiguration.DBA.get());
        LokalDatabaseStøtte.kjørMigreringFor(DatasourceConfiguration.DBA.get());
        LokalDatabaseStøtte.settOppJndiForDefaultDataSource(skjemaer.get());
    }

    private static boolean erMigreringKjørt() {
        File[] list = getSemaphorer();
        if (list.length == 0) {
            LOG.info("Migrering er ikke kjørt");
            return false;
        }

        try {
            BasicFileAttributes attr = Files.readAttributes(list[0].toPath(), BasicFileAttributes.class);
            if (attr.creationTime().toInstant().isBefore(Instant.now().minusSeconds(300))) {
                LOG.info("Migrering ble kjørt for mer enn 5 minutter siden");
                Files.deleteIfExists(list[0].toPath());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static void settSchemaPlaceholder(List<DBConnectionProperties> connectionProperties) {
        for (DBConnectionProperties dbcp : connectionProperties) {
            Matcher matcher = placeholderPattern.matcher(dbcp.getSchema());
            if (matcher.matches()) {
                String placeholder = matcher.group(1);
                if (System.getProperty(placeholder) == null) {
                    System.setProperty(placeholder, dbcp.getDefaultSchema());
                }
            }
        }
    }

    private static void slettAlleSemaphorer() {
        File[] list = getSemaphorer();
        Stream.of(list).forEach(e -> {
            try {
                Files.deleteIfExists(e.toPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private static File[] getSemaphorer() {
        File tmpDir = new File(System.getProperty(TMP_DIR));
        return tmpDir.listFiles((dir, name) ->
                name.startsWith(SEMAPHORE_FIL_PREFIX) && name.endsWith(SEMAPHORE_FIL_SUFFIX));
    }
}
