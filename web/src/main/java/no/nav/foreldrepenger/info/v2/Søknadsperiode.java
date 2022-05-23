package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;

record Søknadsperiode(LocalDate fom, LocalDate tom, KontoType kontoType) {

    no.nav.foreldrepenger.common.innsyn.v2.Søknadsperiode tilDto() {
        var kontoTypeDto = kontoType == null ? null : kontoType.tilDto();
        return new no.nav.foreldrepenger.common.innsyn.v2.Søknadsperiode(fom, tom, kontoTypeDto);
    }
}
