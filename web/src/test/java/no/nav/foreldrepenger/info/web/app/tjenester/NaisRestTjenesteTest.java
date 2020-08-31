package no.nav.foreldrepenger.info.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.web.app.selftest.SelftestService;

public class NaisRestTjenesteTest {

    private NaisRestTjeneste restTjeneste;

    private SelftestService selftestServiceMock = mock(SelftestService.class);

    @BeforeEach
    public void setup() {
        restTjeneste = new NaisRestTjeneste(selftestServiceMock);
    }

    @Test
    public void test_isAlive_skal_returnere_status_200() {
        Response response = restTjeneste.isAlive();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void test_isReady_skal_returnere_service_unavailable_når_kritiske_selftester_feiler() {
        when(selftestServiceMock.kritiskTjenesteFeilet()).thenReturn(true);

        Response response = restTjeneste.isReady();

        assertThat(response.getStatus()).isEqualTo(Response.Status.SERVICE_UNAVAILABLE.getStatusCode());
    }

    @Test
    public void test_isReady_skal_returnere_status_ok_når_selftester_er_ok() {
        when(selftestServiceMock.kritiskTjenesteFeilet()).thenReturn(false);

        Response response = restTjeneste.isReady();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

}