package no.nav.foreldrepenger.info.dbstoette;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

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

        // FIXME (GS): dumt å fange opp runtimeexception
        // Håndtering av verdier som kan inneholde placeholdere
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
}
