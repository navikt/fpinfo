package no.nav.foreldrepenger.info.v2;

enum BehandlingTilstand {
    TIDLIG_SØKNAD, VENTER_PÅ_INNTEKTSMELDING, VENTER_PÅ_DOKUMENTASJON, UNDER_BEHANDLING;

    no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand tilDto() {
        return switch (this) {
            case TIDLIG_SØKNAD -> no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand.TIDLIG_SØKNAD;
            case VENTER_PÅ_INNTEKTSMELDING -> no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand.VENTER_PÅ_INNTEKTSMELDING;
            case VENTER_PÅ_DOKUMENTASJON -> no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand.VENTER_PÅ_DOKUMENTASJON;
            case UNDER_BEHANDLING -> no.nav.foreldrepenger.common.innsyn.v2.BehandlingTilstand.UNDER_BEHANDLING;
        };
    }
}
