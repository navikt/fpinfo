package no.nav.foreldrepenger.info.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.info.web.app.selftest.Selftests;

@ExtendWith(MockitoExtension.class)
class HealthCheckRestServiceTest {

    private HealthCheckRestService restTjeneste;

    @Mock
    private Selftests selftests;

    @BeforeEach
    void setup() {
        restTjeneste = new HealthCheckRestService(selftests);
    }

    @Test
    void skal_returnere_service_unavailable_når_selftester_feiler() {
        when(selftests.isReady()).thenReturn(false);

        var responseReady = restTjeneste.isReady();
        var responseAlive = restTjeneste.isAlive();

        assertThat(responseReady.getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
        assertThat(responseAlive.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    void skal_returnere_status_ok_når_selftester_er_ok() {
        when(selftests.isReady()).thenReturn(true);

        var responseReady = restTjeneste.isReady();
        var responseAlive = restTjeneste.isAlive();

        assertThat(responseReady.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(responseAlive.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

}
