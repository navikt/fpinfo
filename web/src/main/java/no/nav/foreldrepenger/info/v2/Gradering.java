package no.nav.foreldrepenger.info.v2;

import java.math.BigDecimal;

import no.nav.foreldrepenger.common.innsyn.v2.Arbeidsgiver;


record Gradering(Arbeidstidprosent arbeidstidprosent, Aktivitet aktivitet) {

    Gradering(BigDecimal arbeidstidprosent, Aktivitet aktivitet) {
        this(new Arbeidstidprosent(arbeidstidprosent), aktivitet);
    }

    no.nav.foreldrepenger.common.innsyn.v2.Gradering tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.Gradering(arbeidstidprosent.value, map(aktivitet));
    }

    private no.nav.foreldrepenger.common.innsyn.v2.Aktivitet map(Aktivitet aktivitet) {
        if (aktivitet == null) {
            return null;
        }
        return new no.nav.foreldrepenger.common.innsyn.v2.Aktivitet(
                no.nav.foreldrepenger.common.innsyn.v2.Aktivitet.Type.valueOf(aktivitet.type().name()),
                map(aktivitet.arbeidsgiver()));
    }

    private Arbeidsgiver map(no.nav.foreldrepenger.info.v2.Arbeidsgiver arbeidsgiver) {
        if (arbeidsgiver == null) {
            return null;
        }
        return new Arbeidsgiver(arbeidsgiver.id(), Arbeidsgiver.ArbeidsgiverType.valueOf(arbeidsgiver.type().name()));
    }

    record Arbeidstidprosent(BigDecimal value) {

    }
}
