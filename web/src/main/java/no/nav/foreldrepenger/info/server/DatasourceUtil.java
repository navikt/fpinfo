package no.nav.foreldrepenger.info.server;

import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.konfig.Environment;

class DatasourceUtil {
    private static final Environment ENV = Environment.current();

    private DatasourceUtil() {
    }

    static HikariDataSource createDatasource(String dataSourceName, int maxPoolSize) {
        var config = new HikariConfig();
        config.setJdbcUrl(ENV.getRequiredProperty(dataSourceName + ".url"));
        config.setUsername(ENV.getRequiredProperty(dataSourceName + ".username"));
        config.setPassword(ENV.getRequiredProperty(dataSourceName + ".password"));
        config.setSchema("fpinfo_schema"); // er alltid det samme egentlig
        config.setConnectionTimeout(1000);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(maxPoolSize);
        config.setConnectionTestQuery("select 1 from dual");
        config.setDriverClassName("oracle.jdbc.OracleDriver");
        config.setMetricRegistry(Metrics.globalRegistry);

        var dsProperties = new Properties();
        config.setDataSourceProperties(dsProperties);
        return new HikariDataSource(config);
    }
}
