package no.nav.foreldrepenger.info.v2;

record EsÅpenBehandling(BehandlingTilstand tilstand) {
    no.nav.foreldrepenger.common.innsyn.v2.EsÅpenBehandling tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.EsÅpenBehandling(tilstand.tilDto());
    }
}
