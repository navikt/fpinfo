package no.nav.foreldrepenger.info.v2;

enum BehandlingTilstand {
    UNDER_BEHANDLING, VENT_TIDLIG_SØKNAD;

    no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand tilDto() {
        return switch (this) {
            case UNDER_BEHANDLING -> no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand.UNDER_BEHANDLING;
            case VENT_TIDLIG_SØKNAD -> no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand.VENT_TIDLIG_SØKNAD;
        };
    }
}
