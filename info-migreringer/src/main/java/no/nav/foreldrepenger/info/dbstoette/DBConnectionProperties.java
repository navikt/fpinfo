package no.nav.foreldrepenger.info.dbstoette;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class DBConnectionProperties {

    private final String datasource;
    private final String schema;

    private final String effectiveSchema;
    private final String url;
    private final String user;
    private final String password;

    private final String testdataClasspathRoot;
    private final String migrationScriptsFilesystemRoot;
    private final String migrationScriptsClasspathRoot;

    private final String versjonstabell;
    private final boolean defaultDataSource;

    private DBConnectionProperties(Builder builder) {
        this.datasource = builder.datasource;
        this.schema = builder.schema;
        this.effectiveSchema = builder.effectiveSchema;
        this.url = builder.url;
        this.user = builder.user;
        this.password = builder.password;
        this.testdataClasspathRoot = builder.testdataClasspathRoot;
        this.migrationScriptsFilesystemRoot = builder.migrationScriptsFilesystemRoot;
        this.migrationScriptsClasspathRoot = builder.migrationScriptsClasspathRoot;
        this.versjonstabell = builder.versjonstabell;
        this.defaultDataSource = builder.defaultDataSource;
    }

    public static Optional<DBConnectionProperties> finnDefault(List<DBConnectionProperties> connectionProperties) {
        return connectionProperties.stream().filter(DBConnectionProperties::isDefaultDataSource).findFirst();
    }

    public String getDatasource() {
        return datasource;
    }

    public String getSchema() {
        return schema;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getMigrationScriptsFilesystemRoot() {
        return migrationScriptsFilesystemRoot;
    }

    public String getMigrationScriptsClasspathRoot() {
        return migrationScriptsClasspathRoot;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[datasource=" + datasource + ", schema=" + schema + ", effectiveSchema="
                + effectiveSchema + ", url=" + url + ", user=" + user
                + ", testdataClasspathRoot=" + testdataClasspathRoot + ", migrationScriptsFilesystemRoot="
                + migrationScriptsFilesystemRoot + ", migrationScriptsClasspathRoot=" + migrationScriptsClasspathRoot
                + ", versjonstabell=" + versjonstabell + ", defaultDataSource=" + defaultDataSource + ", sqlLoggable="
                + "]";
    }

    public boolean isDefaultDataSource() {
        return defaultDataSource;
    }

    public String getEffectiveSchema() {
        return effectiveSchema;
    }

    public static class Builder {

        private String datasource;
        private String schema;
        private String effectiveSchema;
        private String url;
        private String user;
        private String password;
        private String testdataClasspathRoot;
        private String migrationScriptsFilesystemRoot;
        private String migrationScriptsClasspathRoot;
        private String versjonstabell;
        private boolean defaultDataSource;

        public Builder datasource(String datasource) {
            this.datasource = datasource;
            return this;
        }

        public Builder schema(String schema) {
            this.schema = schema;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder testdataClasspathRoot(String testdataClasspathRoot) {
            this.testdataClasspathRoot = testdataClasspathRoot;
            return this;
        }

        public Builder migrationScriptsFilesystemRoot(String migrationScriptsFilesystemRoot) {
            this.migrationScriptsFilesystemRoot = migrationScriptsFilesystemRoot;
            return this;
        }

        public Builder migrationScriptsClasspathRoot(String migrationScriptsClasspathRoot) {
            this.migrationScriptsClasspathRoot = migrationScriptsClasspathRoot;
            return this;
        }

        public Builder versjonstabell(String versjonstabell) {
            this.versjonstabell = versjonstabell;
            return this;
        }

        public Builder defaultDataSource(boolean defaultDataSource) {
            this.defaultDataSource = defaultDataSource;
            return this;
        }

        public Builder effectiveSchema(String effectiveSchema) {
            this.effectiveSchema = effectiveSchema;
            return this;
        }

        public DBConnectionProperties build() {
            return new DBConnectionProperties(this);
        }

    }
    public static List<DBConnectionProperties> fraStream(InputStream in) throws IOException {
        var m = new ObjectMapper().readValue(in, Wrapper.class);
        return Arrays.stream(m.schemas)
                .map(DBConnectionProperties::map).toList();

    }

    private static DBConnectionProperties map(Schema p) {
        var versjonstabell = "schema_version";
        var url = VariablePlaceholderReplacer.replacePlaceholders(p.url);
        var schema = VariablePlaceholderReplacer.replacePlaceholders(p.schema);
        var defaultSchema =
                p.defaultSchema != null ? VariablePlaceholderReplacer.replacePlaceholders(p.defaultSchema) : schema;
        var user = p.user != null ? VariablePlaceholderReplacer.replacePlaceholders(p.user) : schema;
        var password = p.password != null ? VariablePlaceholderReplacer.replacePlaceholders(p.password) : schema;
        var effectiveSchema = p.effective_schema != null ? VariablePlaceholderReplacer.replacePlaceholders(
                p.effective_schema) : defaultSchema;

        return new Builder()
                .migrationScriptsFilesystemRoot(p.migrationScriptsFilesystemRoot)
                .migrationScriptsClasspathRoot(p.migrationScriptsClasspathRoot)
                .defaultDataSource(p.defaultDataSource)
                .datasource(VariablePlaceholderReplacer.replacePlaceholders(p.datasource))
                .schema(schema)
                .user(user)
                .url(url)
                .effectiveSchema(effectiveSchema)
                .password(password)
                .testdataClasspathRoot(p.testdataClasspathRoot)
                .url(url)
                .versjonstabell(versjonstabell)
                .build();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((datasource == null) ? 0 : datasource.hashCode());
        result = prime * result + (defaultDataSource ? 1231 : 1237);
        result = prime * result + ((effectiveSchema == null) ? 0 : effectiveSchema.hashCode());
        result = prime * result + ((migrationScriptsClasspathRoot == null) ? 0 : migrationScriptsClasspathRoot.hashCode());
        result = prime * result + ((migrationScriptsFilesystemRoot == null) ? 0 : migrationScriptsFilesystemRoot.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((schema == null) ? 0 : schema.hashCode());
        result = prime * result + ((testdataClasspathRoot == null) ? 0 : testdataClasspathRoot.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + ((versjonstabell == null) ? 0 : versjonstabell.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DBConnectionProperties other = (DBConnectionProperties) obj;
        if (datasource == null) {
            if (other.datasource != null)
                return false;
        } else if (!datasource.equals(other.datasource))
            return false;
        if (defaultDataSource != other.defaultDataSource)
            return false;
        if (effectiveSchema == null) {
            if (other.effectiveSchema != null)
                return false;
        } else if (!effectiveSchema.equals(other.effectiveSchema))
            return false;
        if (migrationScriptsClasspathRoot == null) {
            if (other.migrationScriptsClasspathRoot != null)
                return false;
        } else if (!migrationScriptsClasspathRoot.equals(other.migrationScriptsClasspathRoot))
            return false;
        if (migrationScriptsFilesystemRoot == null) {
            if (other.migrationScriptsFilesystemRoot != null)
                return false;
        } else if (!migrationScriptsFilesystemRoot.equals(other.migrationScriptsFilesystemRoot))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (schema == null) {
            if (other.schema != null)
                return false;
        } else if (!schema.equals(other.schema))
            return false;
        if (testdataClasspathRoot == null) {
            if (other.testdataClasspathRoot != null)
                return false;
        } else if (!testdataClasspathRoot.equals(other.testdataClasspathRoot))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        if (versjonstabell == null) {
            if (other.versjonstabell != null)
                return false;
        } else if (!versjonstabell.equals(other.versjonstabell))
            return false;
        return true;
    }
}
