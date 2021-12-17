package no.nav.foreldrepenger.info.v2;

import java.math.BigDecimal;

record Gradering(Arbeidstidprosent arbeidstidprosent) {

    Gradering(BigDecimal arbeidstidprosent) {
        this(new Arbeidstidprosent(arbeidstidprosent));
    }

    no.nav.foreldrepenger.info.v2.dto.Gradering tilDto() {
        return new no.nav.foreldrepenger.info.v2.dto.Gradering(arbeidstidprosent.value);
    }

    static record Arbeidstidprosent(BigDecimal value) {

    }
}
