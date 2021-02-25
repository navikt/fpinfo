package no.nav.foreldrepenger.info.dbstoette;

import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.util.env.Environment;

public final class FlywayKonfig {

    private static final Logger LOG = LoggerFactory.getLogger(FlywayKonfig.class);

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
            LOG.error("Feil under migrering");
            throw flywayException;
        }
    }

    private static Properties lesFlywayPlaceholders() {
        return ENV.getPropertiesWithPrefix("flyway.placeholders.");
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
