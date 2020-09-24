package no.nav.foreldrepenger.info.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.web.app.selftest.Selftests;

public class HealthCheckRestServiceTest {

    private HealthCheckRestService restTjeneste;

    private final Selftests selftests = mock(Selftests.class);

    @BeforeEach
    public void setup() {
        restTjeneste = new HealthCheckRestService(selftests);
    }

    @Test
    public void skal_returnere_service_unavailable_når_selftester_feiler() {
        when(selftests.isReady()).thenReturn(false);

        Response responseReady = restTjeneste.isReady();
        Response responseAlive = restTjeneste.isAlive();

        assertThat(responseReady.getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
        assertThat(responseAlive.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void skal_returnere_status_ok_når_selftester_er_ok() {
        when(selftests.isReady()).thenReturn(true);

        Response responseReady = restTjeneste.isReady();
        Response responseAlive = restTjeneste.isAlive();

        assertThat(responseReady.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(responseAlive.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

}
