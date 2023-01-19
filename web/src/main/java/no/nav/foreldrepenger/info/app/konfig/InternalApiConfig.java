package no.nav.foreldrepenger.info.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import no.nav.foreldrepenger.info.app.metrics.PrometheusRestService;
import no.nav.foreldrepenger.info.app.tjenester.HealthChecks;

@ApplicationPath(InternalApiConfig.API_URL)
public class InternalApiConfig extends Application {

    static final String API_URL = "/internal";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(PrometheusRestService.class, HealthChecks.class);
    }

}
