package no.nav.foreldrepenger.info.web.app.tjenester;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.info.dbstoette.DBConnectionProperties;
import no.nav.foreldrepenger.info.dbstoette.DBConnectionProperties.Builder;
import no.nav.foreldrepenger.info.dbstoette.VariablePlaceholderReplacer;
import no.nav.foreldrepenger.info.web.server.JettyServer;

public class Jalla {

    private static final String DB_SCHEMAS = "jetty_web_server1.json";

    static {
        System.setProperty("defaultds.url", "http://www.vg.no");
        System.setProperty("defaultds.username", "username");
        System.setProperty("defaultds.password", "pw");
        System.setProperty("fpinfoschema.password", "infopw");
        System.setProperty("fpinfoschema.url", "http://www.infoschema.no");
    }

    @Test
    public void testJackson() throws IOException {
        try (InputStream in = JettyServer.class.getResourceAsStream("/" + DB_SCHEMAS)) {
            var m = new ObjectMapper().readValue(in, Wrapper.class);
            var ps = Arrays.stream(m.schemas)
                    .map(this::map).collect(Collectors.toList());
            System.out.println(ps);
        }
    }

    private DBConnectionProperties map(Schema p) {
        String schema;
        String defaultSchema;
        String user;
        String password;
        String effectiveSchema;
        String url = p.url;
        try {
            url = VariablePlaceholderReplacer.replacePlaceholders(p.url);
            schema = VariablePlaceholderReplacer.replacePlaceholders(p.schema);
            defaultSchema = p.defaultSchema != null ? VariablePlaceholderReplacer.replacePlaceholders(p.defaultSchema) : schema;
            user = p.user != null ? VariablePlaceholderReplacer.replacePlaceholders(p.user) : schema;
            password = p.password != null ? VariablePlaceholderReplacer.replacePlaceholders(p.password) : schema;
            effectiveSchema = VariablePlaceholderReplacer.replacePlaceholders(p.effective_schema);
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
                .versjonstabell(p.versjonstabell)
                .build();
    }

}
