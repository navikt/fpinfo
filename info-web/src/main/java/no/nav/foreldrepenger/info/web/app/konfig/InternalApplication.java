package no.nav.foreldrepenger.info.web.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import no.nav.foreldrepenger.info.web.app.metrics.PrometheusRestService;
import no.nav.foreldrepenger.info.web.app.tjenester.HealthChecks;

@ApplicationPath(InternalApplication.API_URL)
public class InternalApplication extends Application {

    static final String API_URL = "/internal";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(PrometheusRestService.class, HealthChecks.class);
    }

}
