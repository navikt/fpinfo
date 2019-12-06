package no.nav.foreldrepenger.info.web.app.metrics;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 * Implementasjon som automatisk setter UTF-8 encoding for JSON resultat.
 */
@ApplicationScoped
public class AppMetricsServlet extends MetricsServlet {

    private static final String KEY_PROSESSTASK = "prosesstask";
    private static final String PROSESS_TASK_TYPE_PREFIX_FORDELING = "info";
    private static final List<String> PROSESS_TASK_TYPE_PREFIXES = Arrays.asList("hendelser");
    private static final String KEY_PREFIX = "fpinfo";
    private transient MetricRepository metricRepository; // NOSONAR
    private transient MetricRegistry registry;  // NOSONAR
    private transient ProsessTaskGaugesCache prosessTaskGaugesCache; //NOSONAR

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        super.doGet(req, resp);  // NOSONAR
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        registrerMetricsForKøedeOgFeiledeProsessTasks();
        super.init(config);
    }

    private void registrerMetricsForKøedeOgFeiledeProsessTasks() {
        List<String> prosessTaskTyper = metricRepository.hentProsessTaskTyperMedPrefixer(PROSESS_TASK_TYPE_PREFIXES);
        for (String ptType : prosessTaskTyper) {
            registry.register(KEY_PREFIX + "." + KEY_PROSESSTASK + ".koet." + ptType, (Gauge<BigDecimal>) () -> prosessTaskGaugesCache.antallProsessTaskerKøet(ptType));
            registry.register(KEY_PREFIX + "." + KEY_PROSESSTASK + ".feilet." + ptType, (Gauge<BigDecimal>) () -> prosessTaskGaugesCache.antallProsessTaskerFeilet(ptType));
        }

        registry.register( // NOSONAR
                KEY_PREFIX + "." + KEY_PROSESSTASK + ".koet." + PROSESS_TASK_TYPE_PREFIX_FORDELING,
                (Gauge<BigDecimal>) () -> prosessTaskGaugesCache.antallProsessTaskerMedTypePrefixKøet(PROSESS_TASK_TYPE_PREFIX_FORDELING));  // NOSONAR
    }

    @Inject
    public void setMetricRepository(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;// NOSONAR
    }


    @Inject
    public void setRegistry(MetricRegistry registry) {
        this.registry = registry;  // NOSONAR
    }

    @Inject
    public void setProsessTaskGaugesCache(ProsessTaskGaugesCache prosessTaskGaugesCache) {
        this.prosessTaskGaugesCache = prosessTaskGaugesCache; //NOSONAR
    }
}
