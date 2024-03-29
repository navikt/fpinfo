package no.nav.foreldrepenger.info.v2;

import java.util.Objects;

record Saksnummer(String value) {

    Saksnummer {
        Objects.requireNonNull(value, "saksnummer kan ikke være null");
    }

    no.nav.foreldrepenger.common.innsyn.Saksnummer tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.Saksnummer(value);
    }
}
