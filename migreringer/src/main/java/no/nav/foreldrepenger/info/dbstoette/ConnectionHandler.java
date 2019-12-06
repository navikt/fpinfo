package no.nav.foreldrepenger.info.dbstoette;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class ConnectionHandler {

    private static Map<String, DataSource> cache = new ConcurrentHashMap<>();

    private ConnectionHandler() {
    }

    public static synchronized DataSource opprettFra(DBConnectionProperties dbProperties, String brukAnnetSkjema) {

        String key = dbProperties.getDatasource() + dbProperties.getSchema();
        if (brukAnnetSkjema != null) {
            key = key + brukAnnetSkjema;
        }
        key = key.toLowerCase();

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        DataSource ds = opprettDatasource(dbProperties, brukAnnetSkjema);
        cache.put(key, ds);

        return ds;
    }

    private static DataSource opprettDatasource(DBConnectionProperties dbProperties, String brukAnnetSkjema) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbProperties.getUrl());
        config.setUsername(dbProperties.getUser());
        config.setPassword(dbProperties.getPassword());
        config.setSchema(dbProperties.getEffectiveSchema());
        if (brukAnnetSkjema != null) {
            config.setSchema(brukAnnetSkjema);
        }
        config.setConnectionTimeout(1000);
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(4);
        config.setConnectionTestQuery("select 1 from dual");
        config.setDriverClassName("oracle.jdbc.OracleDriver");

        Properties dsProperties = new Properties();
        config.setDataSourceProperties(dsProperties);

        HikariDataSource hikariDataSource = new HikariDataSource(config);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> hikariDataSource.close()));

        return hikariDataSource;
    }
}
