package no.nav.foreldrepenger.info.dbstoette;

public class Schema {

    public String user;
    public String effectiveSchema;
    public String datasource;
    public String defaultSchema;
    public String password;
    public String schema;
    public String effective_schema;
    public String url;
    public boolean isdefault;
    public boolean defaultDataSource;
    public String migrationScriptsClasspathRoot;
    public String migrationScriptsFilesystemRoot;
    public String versjonstabell;
    public boolean sqlLoggable;
    public boolean migrateClean;
    public String testdataClasspathRoot;

    @Override
    public String toString() {
        return "Schema [user=" + user + ", effectiveSchema=" + effectiveSchema + ", datasource=" + datasource + ", defaultSchema=" + defaultSchema
                + ", password=" + password + ", schema=" + schema + ", effective_schema=" + effective_schema + ", url=" + url + ", defaultDataSource="
                + defaultDataSource + ", migrationScriptsClasspathRoot=" + migrationScriptsClasspathRoot + ", migrationScriptsFilesystemRoot="
                + migrationScriptsFilesystemRoot + ", versjonstabell=" + versjonstabell + ", sqlLoggable=" + sqlLoggable + ", migrateClean="
                + migrateClean + ", testdataClasspathRoot=" + testdataClasspathRoot + "]";
    }

}