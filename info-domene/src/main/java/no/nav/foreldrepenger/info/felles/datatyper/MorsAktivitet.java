package no.nav.foreldrepenger.info.felles.datatyper;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum MorsAktivitet {

    ARBEID("ARBEID"),
    UTDANNING("UTDANNING"),
    SAMTIDIGUTTAK("SAMTIDIGUTTAK"),
    KVALPROG("KVALPROG"),
    INTROPROG("INTROPROG"),
    TRENGER_HJELP("TRENGER_HJELP"),
    INNLAGT("INNLAGT"),
    ARBEID_OG_UTDANNING("ARBEID_OG_UTDANNING"),
    UFØRE("UFØRE"),
    UKJENT("-");

    private static final Map<String, MorsAktivitet> MORS_AKTIVITET_MAP;

    static {
        Map<String, MorsAktivitet> map = new ConcurrentHashMap<>();
        for (MorsAktivitet morsAktivitet : MorsAktivitet.values()) {
            map.put(morsAktivitet.getVerdi(), morsAktivitet);
        }
        MORS_AKTIVITET_MAP = Collections.unmodifiableMap(map);
    }

    private final String verdi;

    MorsAktivitet(String verdi) {
        this.verdi = verdi;
    }

    public static MorsAktivitet get(String verdi) {
        return Optional.ofNullable(verdi)
                .map(MORS_AKTIVITET_MAP::get)
                .orElse(UKJENT);
    }

    public String getVerdi() {
        return verdi;
    }
}
