package no.nav.foreldrepenger.info.v2.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public record AnnenPart(PersonDetaljer personDetaljer) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public AnnenPart {
        Objects.requireNonNull(personDetaljer, "Persondetaljer kan ikke være null");
    }

    @Override
    @JsonValue
    public PersonDetaljer personDetaljer() {
        return personDetaljer;
    }
}
