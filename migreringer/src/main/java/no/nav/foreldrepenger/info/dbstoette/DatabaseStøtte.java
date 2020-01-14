package no.nav.foreldrepenger.info.dbstoette;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseStøtte {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseStøtte.class);

    private DatabaseStøtte() {
    }

    public static void kjørMigreringFor(List<DBConnectionProperties> connectionProperties) {
        LOG.info("Kjører migreringer for {}", connectionProperties);
        connectionProperties.forEach(DatabaseStøtte::kjørerMigreringFor);
    }

    /**
     * Setter JDNI-oppslag for default skjema
     */
    public static void settOppJndiForDefaultDataSource(List<DBConnectionProperties> allDbConnectionProperties) {
        Optional<DBConnectionProperties> defaultDataSource = DBConnectionProperties
                .finnDefault(allDbConnectionProperties);
        defaultDataSource.ifPresent(DatabaseStøtte::settOppJndiDataSource);
    }

    private static void kjørerMigreringFor(DBConnectionProperties connectionProperties) {
        LOG.info("Kjører migrering for {}", connectionProperties);
        DataSource dataSource = ConnectionHandler.opprettFra(connectionProperties, connectionProperties.getSchema());
        settOppDBSkjema(dataSource, connectionProperties);
    }

    static void settOppJndiDataSource(DBConnectionProperties defaultConnectionProperties) {
        DataSource dataSource = ConnectionHandler.opprettFra(defaultConnectionProperties, null);
        try {
            new EnvEntry("jdbc/" + defaultConnectionProperties.getDatasource(), dataSource); // NOSONAR
        } catch (NamingException e) {
            throw new RuntimeException("Feil under registrering av JDNI-entry for default datasource", e); // NOSONAR
        }
    }

    private static void settOppDBSkjema(DataSource dataSource, DBConnectionProperties dbProperties) {
        if (dbProperties.isMigrateClean()) {
            LOG.error("Feil under migrering");
            throw new IllegalStateException("Feil i konfigurasjon, prod kan ikke cleanes");
        } else {
            migrer(dataSource, dbProperties);
        }
    }

    private static void migrer(DataSource dataSource, DBConnectionProperties connectionProperties) {
        String scriptLocation;
        if (connectionProperties.getMigrationScriptsClasspathRoot() != null) {
            scriptLocation = connectionProperties.getMigrationScriptsClasspathRoot();
        } else {
            scriptLocation = getMigrationScriptLocation(connectionProperties);
        }
        LOG.info("Migrerer med lokasjon {} og datasource {}", scriptLocation, dataSource);
        boolean migreringOk = FlywayKonfig.lagKonfig(dataSource)
                .medSqlLokasjon(scriptLocation)
                .migrerDb();

        if (!migreringOk) {
            LOG.error("Feil under migrering");
            throw new IllegalStateException("Feil i script. Avslutter");
        }
    }

    static String getMigrationScriptLocation(DBConnectionProperties connectionProperties) {
        String relativePath = connectionProperties.getMigrationScriptsFilesystemRoot()
                + connectionProperties.getDatasource();
        File baseDir = new File(".").getAbsoluteFile();
        File location = new File(baseDir, relativePath); // NOSONAR
        while (!location.exists()) {
            baseDir = baseDir.getParentFile();
            if (baseDir == null || !baseDir.isDirectory()) {
                throw new IllegalArgumentException("Klarte ikke finne : " + baseDir);
            }
            location = new File(baseDir, relativePath); // NOSONAR
        }

        return "filesystem:" + location.getPath();
    }

}
