package no.nav.foreldrepenger.info.dbstoette;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LokalDatabaseStøtte {

    private static final Logger LOGGER = LoggerFactory.getLogger(LokalDatabaseStøtte.class);

    private LokalDatabaseStøtte() {
    }

    public static void kjørFullMigreringFor(List<DBConnectionProperties> connectionProperties) {
        LOGGER.info("Kjører full migrering for {}", connectionProperties);
        LokalDatabaseStøtte.nullstill(connectionProperties);
        LokalDatabaseStøtte.kjørMigreringFor(connectionProperties);
    }

    /**
     * Migrering kjøres i vilkårlig rekkefølge. Hvis bruker/skjema angitt i
     * {@link DBConnectionProperties} ikke finnes, opprettes den
     */
    public static void kjørMigreringFor(List<DBConnectionProperties> connectionProperties) {
        LOGGER.info("Kjører migrering for {}", connectionProperties);
        connectionProperties.forEach(LokalDatabaseStøtte::kjørerMigreringFor);
    }

    /**
     * Setter JDNI-oppslag for default skjema
     */
    public static void settOppJndiForDefaultDataSource(List<DBConnectionProperties> allDbConnectionProperties) {
        DatabaseStøtte.settOppJndiForDefaultDataSource(allDbConnectionProperties);
    }

    private static void kjørerMigreringFor(DBConnectionProperties connectionProperties) {
        DataSource dataSource = ConnectionHandler.opprettFra(connectionProperties, connectionProperties.getSchema());
        settOppDBSkjema(dataSource, connectionProperties);
    }

    private static void settOppDBSkjema(DataSource dataSource, DBConnectionProperties dbProperties) {
        migrer(dataSource, dbProperties);
    }

    private static void migrer(DataSource dataSource, DBConnectionProperties connectionProperties) {
        String scriptLocation;
        if (connectionProperties.getMigrationScriptsClasspathRoot() != null) {
            scriptLocation = connectionProperties.getMigrationScriptsClasspathRoot();
        } else {
            scriptLocation = DatabaseStøtte.getMigrationScriptLocation(connectionProperties);
        }
        LOGGER.info("Migrerer {} {}", connectionProperties, scriptLocation);
        boolean migreringOk = LokalFlywayKonfig.lagKonfig(dataSource)
                .medSqlLokasjon(scriptLocation)
                .medCleanup(connectionProperties.isMigrateClean())
                .migrerDb();
        if (!migreringOk) {
            LOGGER.warn(
                    "\n\nKunne ikke starte inkrementell oppdatering av databasen. Det finnes trolig endringer i allerede kjørte script. Kjører full migrering...");

            migreringOk = LokalFlywayKonfig.lagKonfig(dataSource)
                    .medCleanup(true)
                    .medSqlLokasjon(scriptLocation)
                    .migrerDb();
            if (!migreringOk) {
                throw new IllegalStateException("\n\nFeil i script. Avslutter...");
            }
        }
    }

    private static void nullstill(List<DBConnectionProperties> connectionProperties) {
        for (DBConnectionProperties connectionProperty : connectionProperties) {
            DataSource dataSource = ConnectionHandler.opprettFra(connectionProperty, connectionProperty.getSchema());
            LokalFlywayKonfig.lagKonfig(dataSource).nullstill();
        }
    }

}
