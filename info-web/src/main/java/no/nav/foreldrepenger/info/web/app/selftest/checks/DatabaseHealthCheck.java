package no.nav.foreldrepenger.info.web.app.selftest.checks;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.log.metrics.ReadinessAware;

@ApplicationScoped
public class DatabaseHealthCheck implements ReadinessAware {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHealthCheck.class);
    private static final String JDBC_DEFAULT_DS = "jdbc/defaultDS";
    private static final String SQL_QUERY = "select count(1) from BEHANDLING";

    private final String jndiName;

    // må være rask, og bruke et stabilt tabell-navn

    private String endpoint = null; // ukjent frem til første gangs test

    public DatabaseHealthCheck() {
        this.jndiName = JDBC_DEFAULT_DS;
    }

    public String getDescription() {
        return "Test av databaseforbindelse (" + jndiName + ")";
    }

    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public boolean isReady() {
        DataSource dataSource;
        try {
            dataSource = (DataSource) new InitialContext().lookup(jndiName);
        } catch (NamingException e) {
            return false;
        }

        try (Connection connection = dataSource.getConnection()) {
            if (endpoint == null) {
                endpoint = extractEndpoint(connection);
            }
            try (Statement statement = connection.createStatement()) {
                if (!statement.execute(SQL_QUERY)) {
                    throw new SQLException("SQL-spørring ga ikke et resultatsett");
                }
            }
        } catch (SQLException e) {
            LOG.warn("Feil ved SQL-spørring {} mot databasen", SQL_QUERY, e);
            return false;
        }

        return true;
    }

    private String extractEndpoint(Connection connection) {
        String result = "?";
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String url = metaData.getURL();
            if (url != null) {
                if (!url.toUpperCase(Locale.US).contains("SERVICE_NAME=")) { // don't care about Norwegian letters here
                    url = url + "/" + connection.getSchema();
                }
                result = url;
            }
        } catch (SQLException e) {
            // ikke fatalt
        }
        return result;
    }
}
