package no.nav.foreldrepenger.info.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.ToParterTestScenario;
import no.nav.foreldrepenger.info.UttakPeriode;

class UttakTjenesteTest {

    @Test
    void skal_hente_felles_uttaksplan() {
        var scenario = new ToParterTestScenario()
                .uttak(List.of(new UttakPeriode.Builder().build()))
                .uttakAnnenpart(List.of(new UttakPeriode.Builder().build()));

        var repository = scenario.repository();
        var tjeneste = new UttakTjeneste(new BehandlingTjeneste(repository), repository);

        var uttak = tjeneste.hentFellesUttaksplan(scenario.saksnummer(), false);

        assertThat(uttak).hasSize(2);
    }

}
