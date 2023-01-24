package no.nav.foreldrepenger.info.datatyper;

import java.util.Objects;
import java.util.stream.Stream;

public enum MorsAktivitet {

    ARBEID,
    UTDANNING,
    KVALPROG,
    INTROPROG,
    TRENGER_HJELP,
    INNLAGT,
    ARBEID_OG_UTDANNING,
    UFØRE,
    IKKE_OPPGITT;

    public static MorsAktivitet get(String verdi) {
        return Stream.of(values()).filter(ev -> Objects.equals(ev.name(), verdi)).findFirst().orElse(null);
    }
}
