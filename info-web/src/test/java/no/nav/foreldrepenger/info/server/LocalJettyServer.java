package no.nav.foreldrepenger.info.server;


import java.util.List;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import org.eclipse.jetty.webapp.WebAppContext;

import no.nav.foreldrepenger.info.TestTjeneste;
import no.nav.foreldrepenger.info.app.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.info.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.info.app.exceptions.JsonProcessingExceptionMapper;
import no.nav.foreldrepenger.info.app.jackson.JacksonJsonConfig;
import no.nav.foreldrepenger.info.app.konfig.ApplicationConfig;
import no.nav.foreldrepenger.info.dbstoette.DBConnectionProperties;
import no.nav.foreldrepenger.info.dbstoette.DatabaseStøtte;

public class LocalJettyServer extends JettyServer {

    private static final int PORT = 8040;

    public LocalJettyServer(int hostPort) {
        super(hostPort);
    }

    public static void main(String[] args) throws Exception {
        var jettyServer = new LocalJettyServer(PORT);
        migrerLokalDb();
        jettyServer.start();
    }

    private static void migrerLokalDb() {
        var dbProps = dbProps();
        DatabaseStøtte.settOppJndiForDefaultDataSource(dbProps);
        DatabaseStøtte.kjørMigreringFor(dbProps);
    }

    @Override
    protected void addFilters(WebAppContext ctx) {
        //Ingen filter lokalt
    }

    private static List<DBConnectionProperties> dbProps() {
        var fpinfo_schema_props = new DBConnectionProperties.Builder()
                .user("fpinfo_schema")
                .schema("fpinfo_schema")
                .defaultDataSource(true)
                .effectiveSchema("fpinfo_schema")
                .datasource("fpinfoSchema")
                .url("jdbc:oracle:thin:@localhost:1521:XE")
                .migrationScriptsFilesystemRoot("info-migreringer/src/main/resources/db/migration/")
                .build();
        return List.of(fpinfo_schema_props);
    }

    @Override
    protected List<Class<?>> getApplicationClasses() {
        return List.of(LocalApplicationConfig.class);
    }

    @ApplicationPath(ApplicationConfig.API_URI)
    private static class LocalApplicationConfig extends ApplicationConfig {

        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                    TestTjeneste.class,
                    ConstraintViolationMapper.class,
                    JsonProcessingExceptionMapper.class,
                    JacksonJsonConfig.class,
                    GeneralRestExceptionMapper.class);
        }
    }
}

