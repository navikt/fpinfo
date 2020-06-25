package no.nav.foreldrepenger.info.web.app.selftest.checks;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DatabaseHealthCheck extends ExtHealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHealthCheck.class);
    private static final String JDBC_DEFAULT_DS = "jdbc/defaultDS";

    private String jndiName;

    private static final Set<String> SQL_QUERIES = new HashSet<>();

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Oslo"));
        SQL_QUERIES.add("select count(1) from AKSJONSPUNKT");
        SQL_QUERIES.add("select count(1) from SAK_STATUS");
        SQL_QUERIES.add("select count(1) from MOTTATT_DOKUMENT");
        SQL_QUERIES.add("select count(1) from BEHANDLING");
        SQL_QUERIES.add("select count(1) from UTTAK_PERIODE");
        SQL_QUERIES.add("select count(1) from FAGSAK_RELASJON");
        SQL_QUERIES.add("select count(1) from GJELDENDE_VEDTATT_BEHANDLING");
        SQL_QUERIES.add("select count(1) from SOEKNAD_GR");
    }

    private String endpoint = null; // ukjent frem til første gangs test

    public DatabaseHealthCheck() {
        this.jndiName = JDBC_DEFAULT_DS;
    }

    DatabaseHealthCheck(String dsJndiName) {
        this.jndiName = dsJndiName;
    }

    @Override
    protected String getDescription() {
        return "Test av databaseforbindelse (" + jndiName + ")";
    }

    @Override
    protected String getEndpoint() {
        return endpoint;
    }

    @Override
    protected InternalResult performCheck() {

        InternalResult intTestRes = new InternalResult();

        DataSource dataSource = null;
        try {
            dataSource = (DataSource) new InitialContext().lookup(jndiName);
        } catch (NamingException e) {
            intTestRes.setMessage("Feil ved JNDI-oppslag for " + jndiName + " - " + e);
            intTestRes.setException(e);
            return intTestRes;
        }

        Optional<List<InternalResult>> results = performChecks(dataSource);
        if (results.isPresent()) {
            return results.get().get(0);
        }

        intTestRes.noteResponseTime();
        intTestRes.setOk(true);
        return intTestRes;
    }

    private Optional<List<InternalResult>> performChecks(DataSource dataSource) {
        List<InternalResult> results = new ArrayList<>();
        String runningQuery = "";
        try (Connection connection = dataSource.getConnection()) {
            if (endpoint == null) {
                endpoint = extractEndpoint(connection);
            }
            for (String sqlQuery : SQL_QUERIES) {
                runningQuery = sqlQuery;
                try (Statement statement = connection.createStatement()) {
                    if (!statement.execute(sqlQuery)) { // NOSONAR
                        String message = "SQL-spørring '" + sqlQuery + "' feilet";
                        results.add(opprettInternalResult(message, new SQLException(message)));
                    }
                }
            }
        } catch (SQLException e) {
            String error = "Feil ved kjøring av selftest mot db for query (" + runningQuery + ") mot "
                    + JDBC_DEFAULT_DS;
            LOGGER.error(error, e);
            results.add(opprettInternalResult(error, e));
        }
        return results.isEmpty() ? Optional.empty() : Optional.of(results);
    }

    private static InternalResult opprettInternalResult(String message, Exception feil) {
        InternalResult internalResult = new InternalResult();
        internalResult.setMessage(message);
        internalResult.setException(feil);
        return internalResult;
    }

    private static String extractEndpoint(Connection connection) {
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
