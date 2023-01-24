package no.nav.foreldrepenger.info.v2;

enum BehandlingTilstand {
    UNDER_BEHANDLING;

    no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand tilDto() {
        return switch (this) {
            case UNDER_BEHANDLING -> no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand.UNDER_BEHANDLING;
        };
    }
}
