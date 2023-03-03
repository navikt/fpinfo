package no.nav.foreldrepenger.info.v2;

record SvpÅpenBehandling(BehandlingTilstand tilstand) {
    no.nav.foreldrepenger.common.innsyn.SvpÅpenBehandling tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.SvpÅpenBehandling(tilstand.tilDto());
    }
}
