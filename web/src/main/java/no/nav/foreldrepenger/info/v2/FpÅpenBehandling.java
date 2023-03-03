package no.nav.foreldrepenger.info.v2;

import java.util.List;

record FpÅpenBehandling(BehandlingTilstand tilstand, List<UttakPeriode> søknadsperioder) {

    no.nav.foreldrepenger.common.innsyn.FpÅpenBehandling tilDto() {
        var søknadsperioderDto = søknadsperioder.stream().map(UttakPeriode::tilDto).toList();
        return new no.nav.foreldrepenger.common.innsyn.FpÅpenBehandling(tilstand.tilDto(), søknadsperioderDto);
    }
}
