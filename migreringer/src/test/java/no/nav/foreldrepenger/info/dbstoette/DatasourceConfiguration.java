package no.nav.foreldrepenger.info.dbstoette;

import java.io.InputStream;
import java.util.List;

public enum DatasourceConfiguration {
    UNIT_TEST,
    DBA,
    JETTY_DEV_WEB_SERVER;

    private String extension;

    DatasourceConfiguration() {
        this(null);
    }

    DatasourceConfiguration(String extension) {
        if (extension != null) {
            this.extension = extension;
        } else {
            this.extension = ".json";
        }
    }

    public List<DBConnectionProperties> get() {
        String fileName = this.name().toLowerCase() + extension; // NOSONAR
        InputStream io = DatasourceConfiguration.class.getClassLoader().getResourceAsStream(fileName);
        return DBConnectionProperties.fraStream(io);
    }

    public List<DBConnectionProperties> getRaw() {
        String fileName = this.name().toLowerCase() + extension; // NOSONAR
        InputStream io = DatasourceConfiguration.class.getClassLoader().getResourceAsStream(fileName);
        return DBConnectionProperties.rawFraStream(io);
    }
}
