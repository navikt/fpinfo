package no.nav.foreldrepenger.info.v2;

import java.util.Set;

record FpÅpenBehandling(BehandlingTilstand tilstand,
                        Set<Søknadsperiode> søknadsperioder) {

}
