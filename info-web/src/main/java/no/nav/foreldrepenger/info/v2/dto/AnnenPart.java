package no.nav.foreldrepenger.info.v2.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;


public record AnnenPart(PersonDetaljer personDetaljer) {

    @JsonCreator
    public AnnenPart {
        Objects.requireNonNull(personDetaljer, "Persondetaljer kan ikke være null");
    }
}
