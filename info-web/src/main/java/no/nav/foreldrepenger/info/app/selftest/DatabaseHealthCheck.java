package no.nav.foreldrepenger.info.app.selftest;

import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.log.metrics.ReadinessAware;

@ApplicationScoped
public class DatabaseHealthCheck implements ReadinessAware {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHealthCheck.class);
    private static final String SQL_QUERY = "select 1 from dual";
    private final String jdbcURL;
    private final DataSource ds;

    public DatabaseHealthCheck() throws Exception {
        ds = (DataSource) new InitialContext().lookup("fpinfoSchema");
        jdbcURL = ds.getConnection().getMetaData().getURL();
    }

    public String getURL() {
        return jdbcURL;
    }

    @Override
    public boolean isReady() {

        try (var connection = ds.getConnection()) {
            try (var statement = connection.createStatement()) {
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [jdbcURL=" + jdbcURL + ", ds=" + ds + "]";
    }
}
