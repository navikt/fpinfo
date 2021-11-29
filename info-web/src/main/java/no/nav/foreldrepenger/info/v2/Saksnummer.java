package no.nav.foreldrepenger.info.v2;

import java.util.Objects;

record Saksnummer(String value) implements SerializableValue {

    Saksnummer {
        Objects.requireNonNull(value, "saksnummer kan ikke være null");
    }
}
