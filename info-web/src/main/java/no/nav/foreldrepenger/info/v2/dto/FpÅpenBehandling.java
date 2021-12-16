package no.nav.foreldrepenger.info.v2.dto;

import java.util.Set;

public record FpÅpenBehandling(BehandlingTilstand tilstand, Set<Søknadsperiode> søknadsperioder) {
}
