package no.nav.foreldrepenger.info.app.selftest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.log.metrics.LivenessAware;
import no.nav.vedtak.log.metrics.ReadinessAware;

@ApplicationScoped
public class Selftests implements ReadinessAware, LivenessAware {

    private DatabaseHealthCheck databaseHealthCheck;

    @Inject
    public Selftests(DatabaseHealthCheck databaseHealthCheck) {
        this.databaseHealthCheck = databaseHealthCheck;
    }

    Selftests() {

    }

    public Selftests.Resultat run() {
        return new Selftests.Resultat(databaseHealthCheck.isReady(), "DB-sjekk", databaseHealthCheck.getURL());
    }

    @Override
    public boolean isReady() {
        return run().isReady();
    }

    @Override
    public boolean isAlive() {
        return isReady();
    }

    public record Resultat(boolean isReady, String description, String endpoint) {

    }
}
