package no.nav.foreldrepenger.info.dbstoette;

import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.konfig.Environment;

public final class FlywayKonfig {

    private static final Logger LOG = LoggerFactory.getLogger(FlywayKonfig.class);

    private static final Environment ENV = Environment.current();
    protected static final String FLYWAY_SCHEMA_VERSION = "schema_version";
    private final DataSource dataSource;
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
        var flywayKonfig = Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .table(FLYWAY_SCHEMA_VERSION);

        if (sqlLokasjon == null) {
            /*
              Default leter flywayKonfig etter classpath:db/migration. Her vet vi at vi ikke skal
              lete i classpath
             */
            flywayKonfig.locations("denne/stien/finnes/ikke");
        } else {
            flywayKonfig.locations(sqlLokasjon);
        }

        flywayKonfig.configuration(lesFlywayPlaceholders());

        try {
            flywayKonfig.load().migrate();
            return true;
        } catch (FlywayException exception) {
            LOG.error("Feil under migrering database migrering", exception);
            throw exception;
        }
    }

    private static Properties lesFlywayPlaceholders() {
        return ENV.getPropertiesWithPrefix("flyway.placeholders.");
    }
}
