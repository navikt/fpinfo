package no.nav.foreldrepenger.info.v2;

import no.nav.foreldrepenger.common.innsyn.Arbeidsgiver;
import no.nav.foreldrepenger.info.Prosent;


record Gradering(Prosent arbeidstidprosent, Aktivitet aktivitet) {

    no.nav.foreldrepenger.common.innsyn.Gradering tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.Gradering(arbeidstidprosent.decimalValue(), map(aktivitet));
    }

    private no.nav.foreldrepenger.common.innsyn.Aktivitet map(Aktivitet aktivitet) {
        if (aktivitet == null) {
            return null;
        }
        return new no.nav.foreldrepenger.common.innsyn.Aktivitet(
                no.nav.foreldrepenger.common.innsyn.Aktivitet.Type.valueOf(aktivitet.type().name()),
                map(aktivitet.arbeidsgiver()));
    }

    private Arbeidsgiver map(no.nav.foreldrepenger.info.v2.Arbeidsgiver arbeidsgiver) {
        if (arbeidsgiver == null) {
            return null;
        }
        return new Arbeidsgiver(arbeidsgiver.id(), Arbeidsgiver.ArbeidsgiverType.valueOf(arbeidsgiver.type().name()));
    }
}
