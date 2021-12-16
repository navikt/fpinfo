package no.nav.foreldrepenger.info.v2;

import java.util.Objects;


record AnnenPart(AktørId aktørId) {

    AnnenPart {
        Objects.requireNonNull(aktørId, "aktørId kan ikke være null");
    }

    no.nav.foreldrepenger.info.v2.dto.AnnenPart tilDto() {
        return new no.nav.foreldrepenger.info.v2.dto.AnnenPart(aktørId.tilDto());
    }
}
