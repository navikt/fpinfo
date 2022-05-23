package no.nav.foreldrepenger.info.v2;

import java.math.BigDecimal;

record SamtidigUttak(BigDecimal value) {
    no.nav.foreldrepenger.common.innsyn.v2.SamtidigUttak tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.SamtidigUttak(value);
    }
}
