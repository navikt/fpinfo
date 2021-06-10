package no.nav.foreldrepenger.info.datatyper;

import static no.nav.vedtak.util.EnumUtil.match;

import java.util.Optional;
import java.util.function.Predicate;

public enum MorsAktivitet {

    ARBEID,
    UTDANNING,
    SAMTIDIGUTTAK,
    KVALPROG,
    INTROPROG,
    TRENGER_HJELP,
    INNLAGT,
    ARBEID_OG_UTDANNING,
    UFØRE,
    UKJENT;

    public static MorsAktivitet get(String verdi) {
        return Optional.ofNullable(verdi)
                .map(v -> match(MorsAktivitet.class, (Predicate<MorsAktivitet>) (e) -> e.getVerdi().equals(v),
                        UKJENT))
                .orElse(UKJENT);
    }

    public String getVerdi() {
        return this.equals(UKJENT) ? "-" : name();
    }
}
