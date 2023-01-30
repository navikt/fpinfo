package no.nav.foreldrepenger.info.v2;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.info.Aksjonspunkt;

final class SakerFelles {

    private SakerFelles() {
    }

    static BehandlingTilstand finnBehandlingTilstand(Set<Aksjonspunkt> aksjonspunkter) {
        var opprettetAksjonspunkt = aksjonspunkter
                .stream()
                .filter(ap -> ap.getStatus() == Aksjonspunkt.Status.OPPRETTET)
                .collect(Collectors.toSet());

        if (contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.VENT_PGA_FOR_TIDLIG_SØKNAD)) {
            return BehandlingTilstand.VENT_TIDLIG_SØKNAD;
        }
        if (contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.VENT_PÅ_SISTE_AAP_ELLER_DP_MELDEKORT)) {
            return BehandlingTilstand.VENT_MELDEKORT;
        }
        if (contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.VENT_PÅ_KOMPLETT_SØKNAD, Aksjonspunkt.Venteårsak.AVV_DOK)) {
            return BehandlingTilstand.VENT_DOKUMENTASJON;
        }
        return BehandlingTilstand.UNDER_BEHANDLING;
    }

    private static boolean contains(Set<Aksjonspunkt> opprettetAksjonspunkt, Aksjonspunkt.Definisjon def) {
        return contains(opprettetAksjonspunkt, def, null);
    }

    private static boolean contains(Set<Aksjonspunkt> opprettetAksjonspunkt,
                                    Aksjonspunkt.Definisjon def,
                                    Aksjonspunkt.Venteårsak venteårsak) {
        return opprettetAksjonspunkt.stream()
                .anyMatch(a -> a.getDefinisjon() == def && a.getVenteårsak() == venteårsak);
    }
}
