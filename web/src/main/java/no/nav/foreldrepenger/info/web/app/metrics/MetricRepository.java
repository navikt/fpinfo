package no.nav.foreldrepenger.info.web.app.metrics;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import no.nav.vedtak.felles.jpa.OracleVersionChecker;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

@ApplicationScoped
public class MetricRepository {

    private EntityManager entityManager;
    private OracleVersionChecker oracleVersionChecker;

    MetricRepository() {
        // for CDI proxy
    }

    @Inject
    public MetricRepository(@VLPersistenceUnit EntityManager entityManager, OracleVersionChecker oracleVersionChecker) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        Objects.requireNonNull(oracleVersionChecker, "oracleVersionChecker"); //$NON-NLS-1$
        this.entityManager = entityManager;
        this.oracleVersionChecker = oracleVersionChecker;
    }

    List<String> hentProsessTaskTyperMedPrefixer(List<String> prefixer) {
        Query query = entityManager.createNativeQuery("SELECT KODE FROM PROSESS_TASK_TYPE");
        @SuppressWarnings("unchecked")
        List<String> alleProsessTaskTyper = query.getResultList();

        return alleProsessTaskTyper.stream().
                filter(ptType -> stringHarEtAvPrefixer(ptType, prefixer)).
                collect(Collectors.toList());
    }

    /**
     * @return Liste av [type/String, status/String, antall/BigDecimal]
     * <p>
     * Tasks med status FERDIG telles ikke.
     */
    List<Object[]> tellAntallProsessTaskerPerTypeOgStatus() {
        String queryStr;
        if (oracleVersionChecker.isRunningOnExpressEdition()) {
            // Express Edition mangler støtte for spørring på partition
            queryStr = "select task_type, status, count(*) from (select * from prosess_task where status <> 'FERDIG') group by task_type, status";
        } else {
            queryStr = "select task_type, status, count(*) from prosess_task partition (STATUS_KLAR) group by task_type, status " +
                    "UNION select task_type, status, count(*) from prosess_task partition (STATUS_FEILET) group by task_type, status";
        }
        Query query = entityManager.createNativeQuery(queryStr);
        @SuppressWarnings("unchecked")
        List<Object[]> rowList = query.getResultList();
        return rowList;
    }

    private boolean stringHarEtAvPrefixer(String s, List<String> prefixer) {
        return prefixer.stream().anyMatch(s::startsWith);
    }
}
