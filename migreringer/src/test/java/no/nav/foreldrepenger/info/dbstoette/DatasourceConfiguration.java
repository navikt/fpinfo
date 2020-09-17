package no.nav.foreldrepenger.info.dbstoette;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public enum DatasourceConfiguration {
    UNIT_TEST,
    DBA,
    JETTY_DEV_WEB_SERVER;

    private final String extension;

    DatasourceConfiguration() {
        this(null);
    }

    DatasourceConfiguration(String extension) {
        this.extension = Optional.ofNullable(extension).orElse(".json");
    }

    public List<DBConnectionProperties> get() {
        String fileName = this.name().toLowerCase() + extension;
        InputStream io = DatasourceConfiguration.class.getClassLoader().getResourceAsStream(fileName);
        return DBConnectionProperties.fraStream(io);
    }

    public List<DBConnectionProperties> getRaw() {
        String fileName = this.name().toLowerCase() + extension;
        InputStream io = DatasourceConfiguration.class.getClassLoader().getResourceAsStream(fileName);
        return DBConnectionProperties.rawFraStream(io);
    }
}
