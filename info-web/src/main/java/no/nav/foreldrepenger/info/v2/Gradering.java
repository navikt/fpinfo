package no.nav.foreldrepenger.info.v2;

import java.math.BigDecimal;

record Gradering(Arbeidstidprosent arbeidstidprosent) {

    Gradering(BigDecimal arbeidstidprosent) {
        this(new Arbeidstidprosent(arbeidstidprosent));
    }

    no.nav.foreldrepenger.common.innsyn.v2.Gradering tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.Gradering(arbeidstidprosent.value);
    }

    record Arbeidstidprosent(BigDecimal value) {

    }
}
