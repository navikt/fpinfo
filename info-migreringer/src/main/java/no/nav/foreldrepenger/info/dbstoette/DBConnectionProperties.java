package no.nav.foreldrepenger.info.dbstoette;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
 *  "migrateClean": true
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
 * migrateClean: fullmigrering av skjema (default false)<br>
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
    private boolean sqlLoggable;
    private boolean migrateClean;

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
        this.sqlLoggable = builder.sqlLoggable;
        this.migrateClean = builder.migrateClean;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((datasource == null) ? 0 : datasource.hashCode());
        result = prime * result + (defaultDataSource ? 1231 : 1237);
        result = prime * result + ((defaultSchema == null) ? 0 : defaultSchema.hashCode());
        result = prime * result + ((effectiveSchema == null) ? 0 : effectiveSchema.hashCode());
        result = prime * result + (migrateClean ? 1231 : 1237);
        result = prime * result + ((migrationScriptsClasspathRoot == null) ? 0 : migrationScriptsClasspathRoot.hashCode());
        result = prime * result + ((migrationScriptsFilesystemRoot == null) ? 0 : migrationScriptsFilesystemRoot.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((schema == null) ? 0 : schema.hashCode());
        result = prime * result + (sqlLoggable ? 1231 : 1237);
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
        if (migrateClean != other.migrateClean)
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
        if (sqlLoggable != other.sqlLoggable)
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

        try (JsonReader reader = Json.createReader(jsonFil)) {
            JsonArray jsonArray = reader.readObject().getJsonArray("schemas");
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.getJsonObject(i);
                dbProperties.add(read(jsonObject));
            }
        }

        if (dbProperties.stream().filter(DBConnectionProperties::isDefaultDataSource).count() > 1L) {
            throw new IllegalStateException("Kun en dataSource kan være default");
        }

        return dbProperties;
    }

    public static List<DBConnectionProperties> rawFraStream(InputStream jsonFil) {
        List<DBConnectionProperties> dbProperties = new ArrayList<>();

        try (JsonReader reader = Json.createReader(jsonFil)) {
            JsonArray jsonArray = reader.readObject().getJsonArray("schemas");
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.getJsonObject(i);
                dbProperties.add(readRaw(jsonObject));
            }
        }

        if (dbProperties.stream().filter(DBConnectionProperties::isDefaultDataSource).count() > 1L) {
            throw new IllegalStateException("Kun en dataSource kan være default");
        }

        return dbProperties;
    }

    public static Optional<DBConnectionProperties> finnDefault(List<DBConnectionProperties> connectionProperties) {
        return connectionProperties.stream().filter(DBConnectionProperties::isDefaultDataSource).findFirst();
    }

    private static DBConnectionProperties read(JsonObject db) {

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

    private static DBConnectionProperties readRaw(JsonObject db) {

        final String datasource = db.getString("datasource");
        final String schema = db.getString("schema");
        final String defaultSchema = db.getString("defaultSchema", schema);
        final String user = db.getString("user", schema);
        final String password = db.getString("password", schema);
        final String testdataClasspathRoot = db.getString("testdataClasspathRoot", null);
        final String migrationScriptsClasspathRoot = db.getString("migrationScriptsClasspathRoot", null);
        final String migrationScriptsFilesystemRoot = db.getString("migrationScriptsFilesystemRoot", null);
        final String tabell = db.getString("versjonstabell", "schema_version");

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
                .url(db.getString("url"))
                .defaultDataSource(db.getBoolean("default", false))
                .sqlLoggable(db.getBoolean("sqlLoggable", false))
                .migrateClean(db.getBoolean("migrateClean", false))
                .effectiveSchema(db.getString("effective_schema", schema))
                .build();
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

    public String getTestdataClasspathRoot() {
        return testdataClasspathRoot;
    }

    public String getMigrationScriptsFilesystemRoot() {
        return migrationScriptsFilesystemRoot;
    }

    public String getMigrationScriptsClasspathRoot() {
        return migrationScriptsClasspathRoot;
    }

    public String getVersjonstabell() {
        return versjonstabell;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[datasource=" + datasource + ", schema=" + schema + ", effectiveSchema="
                + effectiveSchema + ", defaultSchema=" + defaultSchema + ", url=" + url + ", user=" + user
                + ", testdataClasspathRoot=" + testdataClasspathRoot + ", migrationScriptsFilesystemRoot="
                + migrationScriptsFilesystemRoot + ", migrationScriptsClasspathRoot=" + migrationScriptsClasspathRoot
                + ", versjonstabell=" + versjonstabell + ", defaultDataSource=" + defaultDataSource + ", sqlLoggable="
                + sqlLoggable + ", migrateClean=" + migrateClean + "]";
    }

    public boolean isDefaultDataSource() {
        return defaultDataSource;
    }

    public boolean isSqlLoggable() {
        return sqlLoggable;
    }

    public boolean isMigrateClean() {
        return migrateClean;
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
        private boolean sqlLoggable;
        private boolean migrateClean;

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

        public Builder sqlLoggable(boolean sqlLoggable) {
            this.sqlLoggable = sqlLoggable;
            return this;
        }

        public Builder migrateClean(boolean migrateClean) {
            this.migrateClean = migrateClean;
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
            sqlLoggable = prototype.sqlLoggable;
            migrateClean = prototype.migrateClean;
            this.effectiveSchema = prototype.effectiveSchema;
            return this;
        }

        public DBConnectionProperties build() {
            return new DBConnectionProperties(this);
        }
    }

    public static List<DBConnectionProperties> fraStream1(InputStream in) throws JsonParseException, JsonMappingException, IOException {
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
                .sqlLoggable(p.sqlLoggable)
                .migrateClean(p.migrateClean)
                .testdataClasspathRoot(p.testdataClasspathRoot)
                .url(url)
                .versjonstabell(versjonstabell)
                .build();
    }

}
