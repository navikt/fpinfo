package no.nav.foreldrepenger.info.v2;

record SvpÅpenBehandling(BehandlingTilstand tilstand) {
    no.nav.foreldrepenger.common.innsyn.v2.SvpÅpenBehandling tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.SvpÅpenBehandling(tilstand.tilDto());
    }
}
