package no.nav.foreldrepenger.info.v2;

import static no.nav.foreldrepenger.info.Aksjonspunkt.Definisjon.*;
import static no.nav.foreldrepenger.info.Aksjonspunkt.Status.*;
import static no.nav.foreldrepenger.info.v2.BehandlingTilstand.*;
import static no.nav.foreldrepenger.info.v2.SakerFelles.finnBehandlingTilstand;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import no.nav.foreldrepenger.info.Aksjonspunkt;

class SakerFellesTest {

    @ParameterizedTest
    @EnumSource(Aksjonspunkt.Definisjon.class)
    void avbrutt_ap_gir_tilstand_under_behandling(Aksjonspunkt.Definisjon definisjon) {
        var tilstand = finnBehandlingTilstand(Set.of(aksjonspunkt(definisjon, AVBRUTT)));
        assertThat(tilstand).isEqualTo(UNDER_BEHANDLING);
    }

    @ParameterizedTest
    @EnumSource(Aksjonspunkt.Definisjon.class)
    void utført_ap_gir_tilstand_under_behandling(Aksjonspunkt.Definisjon definisjon) {
        var tilstand = finnBehandlingTilstand(Set.of(aksjonspunkt(definisjon, UTFØRT)));
        assertThat(tilstand).isEqualTo(UNDER_BEHANDLING);
    }

    @Test
    void annet_opprettet_ap_gir_tilstand_under_behandling() {
        var tilstand = finnBehandlingTilstand(Set.of(aksjonspunkt(ANNET, OPPRETTET)));
        assertThat(tilstand).isEqualTo(UNDER_BEHANDLING);
    }

    @Test
    void tidlig_søknad_opprettet_ap_gir_tilstand_tidlig_opprettet() {
        var tilstand = finnBehandlingTilstand(Set.of(aksjonspunkt(VENT_PGA_FOR_TIDLIG_SØKNAD, OPPRETTET)));
        assertThat(tilstand).isEqualTo(VENT_TIDLIG_SØKNAD);
    }

    @Test
    void vent_på_meldekort_opprettet_ap_gir_tilstand_vent_på_meldekort() {
        var tilstand = finnBehandlingTilstand(Set.of(aksjonspunkt(VENT_PÅ_SISTE_AAP_ELLER_DP_MELDEKORT, OPPRETTET)));
        assertThat(tilstand).isEqualTo(VENT_PÅ_MELDEKORT);
    }

    private Aksjonspunkt aksjonspunkt(Aksjonspunkt.Definisjon definisjon, Aksjonspunkt.Status status) {
        return new Aksjonspunkt.Builder().medDefinisjon(definisjon).medStatus(status).build();
    }

}