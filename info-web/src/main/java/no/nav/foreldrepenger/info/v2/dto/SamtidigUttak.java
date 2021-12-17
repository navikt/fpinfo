package no.nav.foreldrepenger.info.v2.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record SamtidigUttak(@JsonValue BigDecimal value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public SamtidigUttak {
    }

    @Override
    public BigDecimal value() {
        return value;
    }
}
