package no.nav.foreldrepenger.info.web.app.selftest;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.info.web.app.selftest.checks.DatabaseHealthCheck;
import no.nav.vedtak.log.metrics.LivenessAware;
import no.nav.vedtak.log.metrics.ReadinessAware;

@ApplicationScoped
public class Selftests implements ReadinessAware, LivenessAware {

    private DatabaseHealthCheck databaseHealthCheck;

    private LocalDateTime sistOppdatertTid = LocalDateTime.now().minusDays(1);

    @Inject
    public Selftests(DatabaseHealthCheck databaseHealthCheck) {
        this.databaseHealthCheck = databaseHealthCheck;
    }

    Selftests() {

    }

    public Selftests.Resultat run() {
        oppdaterSelftestResultatHvisNødvendig();
        return new Selftests.Resultat(isReady(), databaseHealthCheck.getDescription(), databaseHealthCheck.getEndpoint());
    }

    @Override
    public boolean isReady() {
        return run().isReady();
    }

    @Override
    public boolean isAlive() {
        return isReady();
    }

    private synchronized void oppdaterSelftestResultatHvisNødvendig() {
        if (sistOppdatertTid.isBefore(LocalDateTime.now().minusSeconds(30))) {
            sistOppdatertTid = LocalDateTime.now();
        }
    }

    public static class Resultat {
        private final boolean isReady;
        private final String description;
        private final String endpoint;

        public Resultat(boolean isReady, String description, String endpoint) {
            this.isReady = isReady;
            this.description = description;
            this.endpoint = endpoint;
        }

        public boolean isReady() {
            return isReady;
        }

        public String getDescription() {
            return description;
        }

        public String getEndpoint() {
            return endpoint;
        }
    }
}
