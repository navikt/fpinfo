package no.nav.foreldrepenger.docs.db;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import no.nav.foreldrepenger.info.dbstoette.ConnectionHandler;
import no.nav.foreldrepenger.info.dbstoette.DBConnectionProperties;
import no.nav.foreldrepenger.info.dbstoette.Databaseskjemainitialisering;
import no.nav.foreldrepenger.info.dbstoette.DatasourceConfiguration;

@Ignore
public class SjekkDbDokumentasjon {

    private static final String HJELP = "Du har nylig lagt til en ny tabell eller kolonne som ikke er dokumentert ihht. gjeldende regler for dokumentasjon."
            + "\nVennligst gå over sql scriptene og dokumenter tabellene på korrekt måte.";

    private static List<DatasourceSchema> datasourceSchemas = new ArrayList<>();

    private static final class DatasourceSchema {
        private DataSource dataSource;
        private DBConnectionProperties schema;

        private DatasourceSchema(DataSource dataSource, DBConnectionProperties schema) {
            this.dataSource = dataSource;
            this.schema = schema;
        }
    }

    @BeforeClass
    public static void setup() throws FileNotFoundException {
        Databaseskjemainitialisering.migrerUnittestSkjemaer();

        List<no.nav.foreldrepenger.info.dbstoette.DBConnectionProperties> dbConnectionProperties = DatasourceConfiguration.UNIT_TEST.get();

        dbConnectionProperties.forEach(e -> {
            //bør ikke ta med fpsak
            if (!e.getSchema().toLowerCase().contains("fpsak")) {
                datasourceSchemas.add(new DatasourceSchema(ConnectionHandler.opprettFra(e, e.getSchema()), e));
            }
        });
    }

    @Test
    public void sjekk_at_alle_tabeller_er_dokumentert() throws Exception {
        for (DatasourceSchema dataSource : datasourceSchemas) {
            sjekkDokumentert(dataSource);
        }
    }

    @Test
    public void sjekk_at_alle_relevant_kolonner_er_dokumentert() throws Exception {
        for (DatasourceSchema dataSource : datasourceSchemas) {
            sjekkKolonnerDokumentert(dataSource);
        }
    }

    private void sjekkKolonnerDokumentert(DatasourceSchema dataSource) throws SQLException {
        String sql = "select table_name||'.'||column_name from all_col_comments where (comments is null or comments='') and owner=sys_context('userenv', 'current_schema') and (upper(table_name) not like 'SCHEMA_%')"
                + " and upper(column_name) not in ('OPPRETTET_TID', 'ENDRET_TID', 'OPPRETTET_AV', 'ENDRET_AV', 'VERSJON', 'BESKRIVELSE', 'NAVN')";
        int missing = 0;
        try (Connection conn = dataSource.dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                missing++;
                System.err.println("Mangler dokumentasjon for Kolonne: " + rs.getString(1));
            }

        }

        if (missing > 0) {
            Assert.fail("Mangler dokumentasjon for " + missing + " kolonner i skjema " + dataSource.schema.getSchema() + ".\n" + HJELP);
        }
    }

    private void sjekkDokumentert(DatasourceSchema dataSource) throws SQLException {
        String sql = "select table_name from all_tab_comments where (comments is null or comments='') and owner=sys_context('userenv', 'current_schema') and table_name not like 'schema_%'";
        int missing = 0;
        try (Connection conn = dataSource.dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                missing++;
                System.err.println("Mangler dokumentasjon for tabell: " + rs.getString(1));
            }

        }

        if (missing > 0) {
            Assert.fail("Mangler dokumentasjon for " + missing + " tabeller i skjema " + dataSource.schema.getSchema() + "\n" + HJELP);
        }
    }
}
