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
                .map(ap -> ap.getDefinisjon())
                .collect(Collectors.toSet());

        //TODO utvide med flere
        if (opprettetAksjonspunkt.contains(Aksjonspunkt.Definisjon.VENT_PGA_FOR_TIDLIG_SØKNAD)) {
            return BehandlingTilstand.VENT_TIDLIG_SØKNAD;
        }

        return BehandlingTilstand.UNDER_BEHANDLING;
    }


}
