package no.nav.foreldrepenger.info.v2;

import java.util.Set;
import java.util.stream.Collectors;

record FpÅpenBehandling(BehandlingTilstand tilstand, Set<Søknadsperiode> søknadsperioder) {

    no.nav.foreldrepenger.info.v2.dto.FpÅpenBehandling tilDto() {
        var søknadsperioderDto = søknadsperioder.stream().map(sp -> sp.tilDto()).collect(Collectors.toSet());
        return new no.nav.foreldrepenger.info.v2.dto.FpÅpenBehandling(tilstand.tilDto(), søknadsperioderDto);
    }
}
