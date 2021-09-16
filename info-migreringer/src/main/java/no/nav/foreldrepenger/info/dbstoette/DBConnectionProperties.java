package no.nav.foreldrepenger.info.dbstoette;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO(Humle): Skriv tester Enkel representasjon av properties for migrering av
 * skjema med flyway. Tilhørende json ser ca slik ut:
 * <p>
 *
 * <pre>
 * {
 *  "datasource" : "vl_dba",
 *  "schema": "vl_dba",
 *  "url": "jdbc:oracle:thin:@localhost:1521:XE",
 *  "migrationScriptsClasspathRoot": "database/migration/foreldrepenger",
 * }
 * </pre>
 * </p>
 * <p>
 * testdataClasspathRoot: pathen til java-klasser for testdata<br>
 * migrationScriptsFilesystemRoot: filsystemsti hvor migreringsfilene for angitt
 * skjema ligger<br>
 * migrationScriptsClasspathRoot: classpath sti hvor migreringsfilene for angitt
 * skjema ligger<br>
 * defaultDataSource: får JDNI-oppslag som 'java/defaultDS' hvis satt til true
 * (default false)<br>
 * </p>
 * <p>
 * Kan også inneholde placeholdere som leses inn via
 * <code>System.getProperties()</code>
 * </p>
 */
public final class DBConnectionProperties {

    private String datasource;
    private String schema;

    private String effectiveSchema;
    private String defaultSchema;
    private String url;
    private String user;
    private String password;

    private String testdataClasspathRoot;
    private String migrationScriptsFilesystemRoot;
    private String migrationScriptsClasspathRoot;

    private String versjonstabell;
    private boolean defaultDataSource;

    private DBConnectionProperties() {
    }

    private DBConnectionProperties(Builder builder) {
        this.datasource = builder.datasource;
        this.schema = builder.schema;
        this.effectiveSchema = builder.effectiveSchema;
        this.defaultSchema = builder.defaultSchema;
        this.url = builder.url;
        this.user = builder.user;
        this.password = builder.password;
        this.testdataClasspathRoot = builder.testdataClasspathRoot;
        this.migrationScriptsFilesystemRoot = builder.migrationScriptsFilesystemRoot;
        this.migrationScriptsClasspathRoot = builder.migrationScriptsClasspathRoot;
        this.versjonstabell = builder.versjonstabell;
        this.defaultDataSource = builder.defaultDataSource;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((datasource == null) ? 0 : datasource.hashCode());
        result = prime * result + (defaultDataSource ? 1231 : 1237);
        result = prime * result + ((defaultSchema == null) ? 0 : defaultSchema.hashCode());
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
        if (defaultSchema == null) {
            if (other.defaultSchema != null)
                return false;
        } else if (!defaultSchema.equals(other.defaultSchema))
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

    public static List<DBConnectionProperties> fraStream(InputStream jsonFil) {
        List<DBConnectionProperties> dbProperties = new ArrayList<>();
        var objectMapper = new ObjectMapper();

        try {
            var jsonNode = objectMapper.readTree(jsonFil);
            var schemas = jsonNode.get("schemas");
            for (var o : schemas) {
                dbProperties.add(read(o));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        guardBareEnDatasource(dbProperties);

        return dbProperties;
    }

    public static List<DBConnectionProperties> rawFraStream(InputStream jsonFil) {
        List<DBConnectionProperties> dbProperties = new ArrayList<>();
        var objectMapper = new ObjectMapper();

        try {
            var jsonNode = objectMapper.readTree(jsonFil);
            var schemas = jsonNode.get("schemas");
            for (var o : schemas) {
                dbProperties.add(readRaw(o));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        guardBareEnDatasource(dbProperties);

        return dbProperties;
    }

    private static void guardBareEnDatasource(List<DBConnectionProperties> dbProperties) {
        if (dbProperties.stream().filter(DBConnectionProperties::isDefaultDataSource).count() > 1L) {
            throw new IllegalStateException("Kun en dataSource kan være default");
        }
    }

    public static Optional<DBConnectionProperties> finnDefault(List<DBConnectionProperties> connectionProperties) {
        return connectionProperties.stream().filter(DBConnectionProperties::isDefaultDataSource).findFirst();
    }

    private static DBConnectionProperties read(JsonNode db) {

        DBConnectionProperties raw = readRaw(db);
        String schema;
        String defaultSchema;
        String user;
        String password;
        String effectiveSchema;
        String url = raw.getUrl();
        try {
            schema = VariablePlaceholderReplacer.replacePlaceholders(raw.getSchema());
            defaultSchema = VariablePlaceholderReplacer.replacePlaceholders(raw.getDefaultSchema());
            user = VariablePlaceholderReplacer.replacePlaceholders(raw.getUser());
            password = VariablePlaceholderReplacer.replacePlaceholders(raw.getPassword());
            effectiveSchema = VariablePlaceholderReplacer.replacePlaceholders(raw.getEffectiveSchema());
            url = VariablePlaceholderReplacer.replacePlaceholders(raw.getUrl());
        } catch (IllegalStateException e) { // NOSONAR
            user = password = schema = effectiveSchema = defaultSchema = raw.getDefaultSchema();
        }

        return new Builder().fromPrototype(raw)
                .datasource(VariablePlaceholderReplacer.replacePlaceholders(raw.getDatasource()))
                .schema(schema)
                .user(user)
                .defaultSchema(defaultSchema)
                .url(url)
                .effectiveSchema(effectiveSchema)
                .password(password)
                .build();
    }

    private static DBConnectionProperties readRaw(JsonNode db) {

        final String datasource = db.get("datasource").asText();
        final String schema = db.get("schema").asText();
        final String defaultSchema = getText(db, "defaultSchema", schema);
        final String user = getText(db, "user", schema);
        final String password = getText(db, "password", schema);
        final String testdataClasspathRoot = getText(db, "testdataClasspathRoot", null);
        final String migrationScriptsClasspathRoot = getText(db, "migrationScriptsClasspathRoot", null);
        final String migrationScriptsFilesystemRoot = getText(db, "migrationScriptsFilesystemRoot", null);
        final String tabell = getText(db, "versjonstabell", "schema_version");

        return new Builder()
                .datasource(datasource)
                .schema(schema)
                .defaultSchema(defaultSchema)
                .user(user)
                .password(password)
                .migrationScriptsClasspathRoot(migrationScriptsClasspathRoot)
                .migrationScriptsFilesystemRoot(migrationScriptsFilesystemRoot)
                .testdataClasspathRoot(testdataClasspathRoot)
                .versjonstabell(tabell)
                .url(db.get("url").asText())
                .defaultDataSource(getBoolean(db, "default"))
                .effectiveSchema(getText(db, "effective_schema", schema))
                .build();
    }

    private static String getText(JsonNode db, String fieldName, String defaultValue) {
        return db.has(fieldName) ? db.get(fieldName).asText() : defaultValue;
    }

    private static boolean getBoolean(JsonNode db, String fieldName) {
        //Default false
        return db.has(fieldName) && db.get(fieldName).asBoolean();
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
                + effectiveSchema + ", defaultSchema=" + defaultSchema + ", url=" + url + ", user=" + user
                + ", testdataClasspathRoot=" + testdataClasspathRoot + ", migrationScriptsFilesystemRoot="
                + migrationScriptsFilesystemRoot + ", migrationScriptsClasspathRoot=" + migrationScriptsClasspathRoot
                + ", versjonstabell=" + versjonstabell + ", defaultDataSource=" + defaultDataSource + ", sqlLoggable="
                + "]";
    }

    public boolean isDefaultDataSource() {
        return defaultDataSource;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }

    public String getEffectiveSchema() {
        return effectiveSchema;
    }

    public static class Builder {
        private String datasource;
        private String schema;
        private String effectiveSchema;
        private String defaultSchema;
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

        public Builder defaultSchema(String defaultSchema) {
            this.defaultSchema = defaultSchema;
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

        public Builder fromPrototype(DBConnectionProperties prototype) {
            datasource = prototype.datasource;
            schema = prototype.schema;
            defaultSchema = prototype.defaultSchema;
            url = prototype.url;
            user = prototype.user;
            password = prototype.password;
            testdataClasspathRoot = prototype.testdataClasspathRoot;
            migrationScriptsFilesystemRoot = prototype.migrationScriptsFilesystemRoot;
            migrationScriptsClasspathRoot = prototype.migrationScriptsClasspathRoot;
            versjonstabell = prototype.versjonstabell;
            defaultDataSource = prototype.defaultDataSource;
            this.effectiveSchema = prototype.effectiveSchema;
            return this;
        }

        public DBConnectionProperties build() {
            return new DBConnectionProperties(this);
        }
    }

    public static List<DBConnectionProperties> fraStream1(InputStream in) throws IOException {
        var m = new ObjectMapper().readValue(in, Wrapper.class);
        return Arrays.stream(m.schemas)
                .map(DBConnectionProperties::map).toList();

    }

    private static DBConnectionProperties map(Schema p) {
        String schema;
        String defaultSchema;
        String user;
        String password;
        String effectiveSchema;
        String url = p.url;
        String versjonstabell = "schema_version";
        try {
            url = VariablePlaceholderReplacer.replacePlaceholders(p.url);
            schema = VariablePlaceholderReplacer.replacePlaceholders(p.schema);
            defaultSchema = p.defaultSchema != null ? VariablePlaceholderReplacer.replacePlaceholders(p.defaultSchema) : schema;
            user = p.user != null ? VariablePlaceholderReplacer.replacePlaceholders(p.user) : schema;
            password = p.password != null ? VariablePlaceholderReplacer.replacePlaceholders(p.password) : schema;
            effectiveSchema = p.effective_schema != null ? VariablePlaceholderReplacer.replacePlaceholders(p.effective_schema) : defaultSchema;
        } catch (IllegalStateException e) {
            user = password = schema = effectiveSchema = defaultSchema = p.defaultSchema;
        }

        return new Builder()
                .migrationScriptsFilesystemRoot(p.migrationScriptsFilesystemRoot)
                .migrationScriptsClasspathRoot(p.migrationScriptsClasspathRoot)
                .defaultSchema(defaultSchema)
                .defaultDataSource(p.defaultDataSource)
                .datasource(VariablePlaceholderReplacer.replacePlaceholders(p.datasource))
                .schema(schema)
                .user(user)
                .defaultSchema(defaultSchema)
                .url(url)
                .effectiveSchema(effectiveSchema)
                .password(password)
                .testdataClasspathRoot(p.testdataClasspathRoot)
                .url(url)
                .versjonstabell(versjonstabell)
                .build();
    }

}
