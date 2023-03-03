package no.nav.foreldrepenger.info.v2;

record EsÅpenBehandling(BehandlingTilstand tilstand) {
    no.nav.foreldrepenger.common.innsyn.EsÅpenBehandling tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.EsÅpenBehandling(tilstand.tilDto());
    }
}
