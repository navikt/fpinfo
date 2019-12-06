package no.nav.foreldrepenger.info.tjenester.dto;

import java.util.Arrays;

public enum ForsendelseStatus {
    INNVILGET("behandling_innvilget"),
    AVSLÅTT("behandling_avslått"),
    PÅGÅR("behandling_pågar"),
    PÅ_VENT("behandling_på_vent"),
    MOTTATT("forsendelse_mottatt");

    private final String value;

    ForsendelseStatus(String value) {
        this.value = value;
    }

    /**
     * @return the Enum representation for the given string.
     * @throws IllegalArgumentException if unknown string.
     */
    public static ForsendelseStatus asEnumValue(String s) {
        return Arrays.stream(values())
                .filter(v -> v.value.equals(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ugyldig verdi: " + s));
    }
}
