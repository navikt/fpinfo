package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;

record Søknadsperiode(LocalDate fom, LocalDate tom, KontoType kontoType) {

    no.nav.foreldrepenger.info.v2.dto.Søknadsperiode tilDto() {
        var kontoTypeDto = kontoType == null ? null : kontoType.tilDto();
        return new no.nav.foreldrepenger.info.v2.dto.Søknadsperiode(fom, tom, kontoTypeDto);
    }
}
