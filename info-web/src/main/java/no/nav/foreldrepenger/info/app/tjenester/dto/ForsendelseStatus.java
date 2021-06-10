package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.vedtak.util.EnumUtil.matchOrThrow;

import java.util.Optional;
import java.util.function.Predicate;

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
    public static ForsendelseStatus asEnumValue(String verdi) {
        return Optional.ofNullable(verdi)
                .map(v -> matchOrThrow(ForsendelseStatus.class, (Predicate<ForsendelseStatus>) (e) -> e.value.equals(v),
                        () -> new IllegalArgumentException("Ugyldig verdi: " + v)))
                .orElseThrow();
    }
}
