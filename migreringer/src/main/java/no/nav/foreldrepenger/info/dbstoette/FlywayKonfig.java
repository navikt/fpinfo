package no.nav.foreldrepenger.info.dbstoette;

import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.konfig.StandardPropertySource;
import no.nav.vedtak.util.env.Environment;

public final class FlywayKonfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayKonfig.class);

    private static final Environment ENV = Environment.current();
    private DataSource dataSource;
    private String sqlLokasjon = null;

    private FlywayKonfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static FlywayKonfig lagKonfig(DataSource dataSource) {
        return new FlywayKonfig(dataSource);
    }

    public FlywayKonfig medSqlLokasjon(String sqlLokasjon) {
        this.sqlLokasjon = sqlLokasjon;
        return this;
    }

    public boolean migrerDb() {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(dataSource);

        if (sqlLokasjon != null) {
            flyway.setLocations(sqlLokasjon);
        } else {
            /**
             * Default leter flyway etter classpath:db/migration. Her vet vi at vi ikke skal
             * lete i classpath
             */
            flyway.setLocations("denne/stien/finnes/ikke");
        }

        flyway.configure(lesFlywayPlaceholders());

        try {
            flyway.migrate();
            return true;
        } catch (FlywayException flywayException) {
            LOGGER.error("Feil under migrering");
            throw flywayException;
        }
    }

    private static Properties lesFlywayPlaceholders() {
        Properties placeholders = new Properties();
        Properties placeholders1 = new Properties();
        placeholders1.putAll(ENV.getProperties(StandardPropertySource.SYSTEM_PROPERTIES).getVerdier());
        placeholders1.putAll(ENV.getProperties(StandardPropertySource.ENV_PROPERTIES).getVerdier());
        placeholders1.putAll(ENV.getProperties(StandardPropertySource.APP_PROPERTIES).getVerdier());

        // ny
        var mapProp = placeholders1.entrySet()
                .stream()
                .filter(k -> k.getKey().toString().startsWith("flyway.placeholders."))
                .collect(
                        Collectors.toMap(
                                e -> (String) e.getKey(),
                                e -> (String) e.getValue()));
        var filtered = new Properties();
        filtered.putAll(mapProp);
        LOGGER.info("Alternative filtering " + filtered);

        // gammel
        for (String prop : System.getProperties().stringPropertyNames()) {
            if (prop.startsWith("flyway.placeholders.")) {
                placeholders.setProperty(prop, System.getProperty(prop));
            }
        }
        return placeholders;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
