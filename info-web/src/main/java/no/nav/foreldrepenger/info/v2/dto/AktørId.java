package no.nav.foreldrepenger.info.v2.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AktørId(String value) implements PersonDetaljer {

    @JsonCreator
    public AktørId {
        Objects.requireNonNull(value, "AktørId kan ikke være null");
    }

    @JsonProperty("aktørId")
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "***";
    }
}
