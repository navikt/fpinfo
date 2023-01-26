package no.nav.foreldrepenger.info.v2;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import no.nav.foreldrepenger.info.Aksjonspunkt;

class SakerFellesTest {

    @ParameterizedTest
    @EnumSource(Aksjonspunkt.Definisjon.class)
    void avbrutt_ap_gir_status_under_behandling(Aksjonspunkt.Definisjon definisjon) {
        var tilstand = SakerFelles.finnBehandlingTilstand(Set.of(aksjonspunkt(definisjon, Aksjonspunkt.Status.AVBRUTT)));
        assertThat(tilstand).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
    }

    @ParameterizedTest
    @EnumSource(Aksjonspunkt.Definisjon.class)
    void utført_ap_gir_status_under_behandling(Aksjonspunkt.Definisjon definisjon) {
        var tilstand = SakerFelles.finnBehandlingTilstand(Set.of(aksjonspunkt(definisjon, Aksjonspunkt.Status.UTFØRT)));
        assertThat(tilstand).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
    }

    @Test
    void annet_opprettet_ap_gir_status_under_behandling() {
        var tilstand = SakerFelles.finnBehandlingTilstand(Set.of(aksjonspunkt(Aksjonspunkt.Definisjon.ANNET, Aksjonspunkt.Status.OPPRETTET)));
        assertThat(tilstand).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
    }

    @Test
    void tidlig_søknad_opprettet_ap_gir_status_tidlig_opprettet() {
        var tilstand = SakerFelles.finnBehandlingTilstand(Set.of(aksjonspunkt(Aksjonspunkt.Definisjon.VENT_PGA_FOR_TIDLIG_SØKNAD, Aksjonspunkt.Status.OPPRETTET)));
        assertThat(tilstand).isEqualTo(BehandlingTilstand.VENT_TIDLIG_SØKNAD);
    }

    private Aksjonspunkt aksjonspunkt(Aksjonspunkt.Definisjon definisjon, Aksjonspunkt.Status status) {
        return new Aksjonspunkt.Builder().medDefinisjon(definisjon).medStatus(status).build();
    }

}