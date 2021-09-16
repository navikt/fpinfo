package no.nav.foreldrepenger.info.dbstoette;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Schema {

    String user;
    String effectiveSchema;
    String datasource;
    String defaultSchema;
    String password;
    String schema;
    String effective_schema;
    String url;
    @JsonProperty("default")
    boolean defaultDataSource;
    String migrationScriptsClasspathRoot;
    String migrationScriptsFilesystemRoot;
    String versjonstabell;
    boolean sqlLoggable;
    String testdataClasspathRoot;

    @Override
    public String toString() {
        return "Schema [user=" + user + ", effectiveSchema=" + effectiveSchema + ", datasource=" + datasource + ", defaultSchema=" + defaultSchema
                + ", password=" + password + ", schema=" + schema + ", effective_schema=" + effective_schema + ", url=" + url + ", defaultDataSource="
                + defaultDataSource + ", migrationScriptsClasspathRoot=" + migrationScriptsClasspathRoot + ", migrationScriptsFilesystemRoot="
                + migrationScriptsFilesystemRoot + ", versjonstabell=" + versjonstabell + ", sqlLoggable=" + sqlLoggable +
                ", testdataClasspathRoot=" + testdataClasspathRoot + "]";
    }

}
