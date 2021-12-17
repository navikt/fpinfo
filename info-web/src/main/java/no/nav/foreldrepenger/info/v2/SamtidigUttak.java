package no.nav.foreldrepenger.info.v2;

import java.math.BigDecimal;

record SamtidigUttak(BigDecimal value) {
    no.nav.foreldrepenger.info.v2.dto.SamtidigUttak tilDto() {
        return new no.nav.foreldrepenger.info.v2.dto.SamtidigUttak(value);
    }
}
