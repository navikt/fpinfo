package no.nav.foreldrepenger.info.web.app.tjenester;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import no.nav.foreldrepenger.info.web.app.selftest.Selftests;

@Path("/health")
@Produces(TEXT_PLAIN)
@RequestScoped
public class HealthCheckRestService {

    private static final String RESPONSE_CACHE_KEY = "Cache-Control";
    private static final String RESPONSE_CACHE_VAL = "must-revalidate,no-cache,no-store";
    private static final String RESPONSE_OK = "OK";

    private Selftests selftests;

    public HealthCheckRestService() {
        // CDI
    }

    @Inject
    public HealthCheckRestService(Selftests selftests) {
        this.selftests = selftests;
    }

    /**
     * Bruk annet svar enn 200 kun dersom man ønsker at Nais skal restarte pod
     */
    @GET
    @Path("/isAlive")
    @Operation(description = "sjekker om poden lever", tags = "nais", hidden = true)
    public Response isAlive() {
        return Response
                .ok(RESPONSE_OK)
                .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                .build();
    }

    /**
     * Bruk annet svar enn 200 dersom man ønsker trafikk dirigert vekk (eller få nais til å oppskalere)
     */
    @GET
    @Path("isReady")
    @Operation(description = "sjekker om poden er klar", tags = "nais", hidden = true)
    public Response isReady() {
        if (selftests.isReady()) {
            return Response.ok(RESPONSE_OK)
                    .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                    .build();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .header(RESPONSE_CACHE_KEY, RESPONSE_CACHE_VAL)
                .build();
    }
}
