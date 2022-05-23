package no.nav.foreldrepenger.info.datatyper;

import static no.nav.vedtak.util.EnumUtil.match;

import java.util.Optional;

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
        return Optional.ofNullable(verdi)
                .map(v -> match(MorsAktivitet.class, (e) -> e.name().equals(v), null))
                .orElse(null);
    }
}
