package no.nav.foreldrepenger.info.felles.datatyper;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Map<String, MorsAktivitet> MORS_AKTIVITET_MAP;

    static {
        Map<String, MorsAktivitet> map = new ConcurrentHashMap<>();
        for (var morsAktivitet : MorsAktivitet.values()) {
            map.put(morsAktivitet.getVerdi(), morsAktivitet);
        }
        MORS_AKTIVITET_MAP = Map.copyOf(map);
    }

    public static MorsAktivitet get(String verdi) {
        return Optional.ofNullable(verdi)
                .map(MORS_AKTIVITET_MAP::get)
                .orElse(UKJENT);
    }

    public String getVerdi() {
        return this.equals(UKJENT) ? "-" : name();
    }
}
