package no.nav.foreldrepenger.info.v2;

import java.util.Objects;

record AktørId(String value) {

    AktørId {
        Objects.requireNonNull(value, "AktørId kan ikke være null");
    }

    @Override
    public String toString() {
        return "***";
    }

    no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.AktørId tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.AktørId(value);
    }
}
