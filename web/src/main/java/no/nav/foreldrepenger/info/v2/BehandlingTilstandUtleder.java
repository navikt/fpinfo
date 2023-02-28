package no.nav.foreldrepenger.info.v2;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.Aksjonspunkt;

final class BehandlingTilstandUtleder {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingTilstandUtleder.class);

    private BehandlingTilstandUtleder() {
    }

    static BehandlingTilstand utled(Set<Aksjonspunkt> aksjonspunkter) {
        var opprettetAksjonspunkt = aksjonspunkter
                .stream()
                .filter(ap -> ap.getStatus() == Aksjonspunkt.Status.OPPRETTET)
                .collect(Collectors.toSet());

        var tilstand = utledGittOpprettetAksjonspunkt(opprettetAksjonspunkt);
        LOG.info("Utledet behandlingtilstand {} for aksjonspunkter {}", tilstand, aksjonspunkter);
        return tilstand;
    }

    private static BehandlingTilstand utledGittOpprettetAksjonspunkt(Set<Aksjonspunkt> opprettetAksjonspunkt) {
        if (contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.VENT_PGA_FOR_TIDLIG_SØKNAD)) {
            return BehandlingTilstand.VENT_TIDLIG_SØKNAD;
        }
        if (venterPåMeldekort(opprettetAksjonspunkt)) {
            return BehandlingTilstand.VENT_MELDEKORT;
        }
        if (contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.VENT_PÅ_KOMPLETT_SØKNAD, Aksjonspunkt.Venteårsak.AVV_DOK)) {
            return BehandlingTilstand.VENT_DOKUMENTASJON;
        }
        if (venterPåInntektsmelding(opprettetAksjonspunkt)) {
            return BehandlingTilstand.VENT_INNTEKTSMELDING;
        }
        return BehandlingTilstand.UNDER_BEHANDLING;
    }

    private static boolean venterPåMeldekort(Set<Aksjonspunkt> opprettetAksjonspunkt) {
        return contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.VENT_PÅ_SISTE_AAP_ELLER_DP_MELDEKORT)
            || contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.MANUELT_SATT_PÅ_VENT, Aksjonspunkt.Venteårsak.VENT_PÅ_SISTE_AAP_MELDEKORT);
    }

    private static boolean venterPåInntektsmelding(Set<Aksjonspunkt> opprettetAksjonspunkt) {
        return contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.VENT_PÅ_KOMPLETT_SØKNAD, Aksjonspunkt.Venteårsak.VENT_OPDT_INNTEKTSMELDING)
            || contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.VENT_ETTERLYST_INNTEKTSMELDING)
            || contains(opprettetAksjonspunkt, Aksjonspunkt.Definisjon.MANUELT_SATT_PÅ_VENT, Aksjonspunkt.Venteårsak.VENT_OPDT_INNTEKTSMELDING);
    }

    private static boolean contains(Set<Aksjonspunkt> opprettetAksjonspunkt, Aksjonspunkt.Definisjon def) {
        return contains(opprettetAksjonspunkt, def, null);
    }

    private static boolean contains(Set<Aksjonspunkt> opprettetAksjonspunkt,
                                    Aksjonspunkt.Definisjon def,
                                    Aksjonspunkt.Venteårsak venteårsak) {
        return opprettetAksjonspunkt.stream()
                .anyMatch(a -> a.getDefinisjon() == def && (venteårsak == null || a.getVenteårsak() == venteårsak));
    }
}
