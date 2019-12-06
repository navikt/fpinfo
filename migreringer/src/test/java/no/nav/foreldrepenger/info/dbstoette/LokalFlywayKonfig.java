package no.nav.foreldrepenger.info.dbstoette;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

public class LokalFlywayKonfig {

    private boolean cleanup = false;
    private FlywayKonfig flywayKonfig;

    private LokalFlywayKonfig() {
    }

    public static LokalFlywayKonfig lagKonfig(DataSource dataSource) {
        LokalFlywayKonfig konfig = new LokalFlywayKonfig();
        konfig.flywayKonfig = FlywayKonfig.lagKonfig(dataSource);
        return konfig;
    }

    public LokalFlywayKonfig medCleanup(boolean utførFullMigrering) {
        this.cleanup = utførFullMigrering;
        return this;
    }

    public LokalFlywayKonfig medSqlLokasjon(String sqlLokasjon) {
        flywayKonfig.medSqlLokasjon(sqlLokasjon);
        return this;
    }

    public boolean migrerDb() {
        if (cleanup) {
            nullstill();
        }
        return flywayKonfig.migrerDb();
    }

    public void nullstill() {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(flywayKonfig.getDataSource());
        flyway.clean();
    }
}
