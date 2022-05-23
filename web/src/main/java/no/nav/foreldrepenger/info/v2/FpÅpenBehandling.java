package no.nav.foreldrepenger.info.v2;

import java.util.Set;
import java.util.stream.Collectors;

record FpÅpenBehandling(BehandlingTilstand tilstand, Set<Søknadsperiode> søknadsperioder) {

    no.nav.foreldrepenger.common.innsyn.v2.FpÅpenBehandling tilDto() {
        var søknadsperioderDto = søknadsperioder.stream().map(Søknadsperiode::tilDto).collect(Collectors.toSet());
        return new no.nav.foreldrepenger.common.innsyn.v2.FpÅpenBehandling(tilstand.tilDto(), søknadsperioderDto);
    }
}
