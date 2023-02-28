package no.nav.foreldrepenger.info.v2;

import static no.nav.foreldrepenger.info.Aksjonspunkt.Definisjon.*;
import static no.nav.foreldrepenger.info.Aksjonspunkt.Status.*;
import static no.nav.foreldrepenger.info.Aksjonspunkt.Venteårsak.*;
import static no.nav.foreldrepenger.info.v2.BehandlingTilstand.*;
import static no.nav.foreldrepenger.info.v2.BehandlingTilstandUtleder.utled;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import no.nav.foreldrepenger.info.Aksjonspunkt;

class BehandlingTilstandUtlederTest {

    @ParameterizedTest
    @EnumSource(Aksjonspunkt.Definisjon.class)
    void avbrutt_ap_gir_tilstand_under_behandling(Aksjonspunkt.Definisjon definisjon) {
        var tilstand = utled(Set.of(aksjonspunkt(definisjon, AVBRUTT)));
        assertThat(tilstand).isEqualTo(UNDER_BEHANDLING);
    }

    @ParameterizedTest
    @EnumSource(Aksjonspunkt.Definisjon.class)
    void utført_ap_gir_tilstand_under_behandling(Aksjonspunkt.Definisjon definisjon) {
        var tilstand = utled(Set.of(aksjonspunkt(definisjon, UTFØRT)));
        assertThat(tilstand).isEqualTo(UNDER_BEHANDLING);
    }

    @Test
    void annet_opprettet_ap_gir_tilstand_under_behandling() {
        var tilstand = utled(Set.of(aksjonspunkt(ANNET, OPPRETTET)));
        assertThat(tilstand).isEqualTo(UNDER_BEHANDLING);
    }

    @Test
    void tidlig_søknad_opprettet_ap_gir_tilstand_tidlig_opprettet() {
        var tilstand = utled(Set.of(aksjonspunkt(VENT_PGA_FOR_TIDLIG_SØKNAD, OPPRETTET)));
        assertThat(tilstand).isEqualTo(VENT_TIDLIG_SØKNAD);
    }

    @Test
    void vent_på_meldekort_opprettet_ap_gir_tilstand_vent_på_meldekort() {
        var tilstand = utled(Set.of(aksjonspunkt(VENT_PÅ_SISTE_AAP_ELLER_DP_MELDEKORT, OPPRETTET)));
        assertThat(tilstand).isEqualTo(VENT_MELDEKORT);
    }

    @Test
    void vent_på_meldekort_manuelt_opprettet_ap_gir_tilstand_vent_på_meldekort() {
        var tilstand = utled(Set.of(aksjonspunkt(MANUELT_SATT_PÅ_VENT, OPPRETTET, VENT_PÅ_SISTE_AAP_MELDEKORT)));
        assertThat(tilstand).isEqualTo(VENT_MELDEKORT);
    }

    @Test
    void vent_på_meldekort_manuelt_opprettet_ap_uten_ventårsak_gir_under_behandling_tilstand() {
        var tilstand = utled(Set.of(aksjonspunkt(MANUELT_SATT_PÅ_VENT, OPPRETTET, AVV_DOK)));
        assertThat(tilstand).isEqualTo(UNDER_BEHANDLING);
    }

    @Test
    void vent_på_komplett_søknad_med_årsak_dok_gir_vent_på_dok_tilstand() {
        var tilstand = utled(Set.of(aksjonspunkt(VENT_PÅ_KOMPLETT_SØKNAD, OPPRETTET, AVV_DOK)));
        assertThat(tilstand).isEqualTo(VENT_DOKUMENTASJON);
    }

    @Test
    void vent_på_komplett_søknad_med_årsak_null_gir_under_behandling_tilstand() {
        var tilstand = utled(Set.of(aksjonspunkt(VENT_PÅ_KOMPLETT_SØKNAD, OPPRETTET, null)));
        assertThat(tilstand).isEqualTo(UNDER_BEHANDLING);
    }

    @Test
    void vent_på_komplett_søknad_med_årsak_inntektsmelding_gir_vent_på_im_tilstand() {
        var tilstand = utled(Set.of(aksjonspunkt(VENT_PÅ_KOMPLETT_SØKNAD, OPPRETTET, VENT_OPDT_INNTEKTSMELDING)));
        assertThat(tilstand).isEqualTo(VENT_INNTEKTSMELDING);
    }

    @Test
    void vent_på_etterlyst_im_med_årsak_inntektsmelding_gir_vent_på_im_tilstand() {
        var tilstand = utled(Set.of(aksjonspunkt(VENT_ETTERLYST_INNTEKTSMELDING, OPPRETTET, VENT_OPDT_INNTEKTSMELDING)));
        assertThat(tilstand).isEqualTo(VENT_INNTEKTSMELDING);
    }

    @Test
    void vent_på_etterlyst_im_med_årsak_null_gir_vent_på_im_tilstand() {
        var tilstand = utled(Set.of(aksjonspunkt(VENT_ETTERLYST_INNTEKTSMELDING, OPPRETTET, null)));
        assertThat(tilstand).isEqualTo(VENT_INNTEKTSMELDING);
    }

    @Test
    void vent_på_inntektsmelding_manuelt_opprettet_ap_gir_tilstand_vent_på_im() {
        var tilstand = utled(Set.of(aksjonspunkt(MANUELT_SATT_PÅ_VENT, OPPRETTET, VENT_OPDT_INNTEKTSMELDING)));
        assertThat(tilstand).isEqualTo(VENT_INNTEKTSMELDING);
    }

    private Aksjonspunkt aksjonspunkt(Aksjonspunkt.Definisjon definisjon, Aksjonspunkt.Status status) {
        return aksjonspunkt(definisjon, status, null);
    }

    private Aksjonspunkt aksjonspunkt(Aksjonspunkt.Definisjon definisjon, Aksjonspunkt.Status status, Aksjonspunkt.Venteårsak venteårsak) {
        return new Aksjonspunkt.Builder().medDefinisjon(definisjon).medStatus(status).medVenteårsak(venteårsak).build();
    }

}
