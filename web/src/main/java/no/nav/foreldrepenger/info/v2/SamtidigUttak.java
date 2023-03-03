package no.nav.foreldrepenger.info.v2;

import no.nav.foreldrepenger.info.Prosent;

record SamtidigUttak(Prosent value) {
    no.nav.foreldrepenger.common.innsyn.SamtidigUttak tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.SamtidigUttak(value.decimalValue());
    }
}
