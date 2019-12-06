package no.nav.foreldrepenger.info.felles.datatyper;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum GraderingAvslagÅrsak {
    UKJENT("-"),
    GRADERING_FØR_UKE_7("4504"),
    FOR_SEN_SØKNAD("4501"),
    MANGLENDE_GRADERINGSAVTALE("4502"),
    MOR_OPPFYLLER_IKKE_AKTIVITETSKRAV("4503"),
    AVSLAG_PGA_100_PROSENT_ARBEID("4523");

    private static final Map<String, GraderingAvslagÅrsak> GRADERING_AVSLAG_ÅRSAK_MAP_MAP;

    static {
        Map<String, GraderingAvslagÅrsak> map = new ConcurrentHashMap<>();
        for (GraderingAvslagÅrsak graderingAvslagÅrsak : GraderingAvslagÅrsak.values()) {
            map.put(graderingAvslagÅrsak.getVerdi(), graderingAvslagÅrsak);
        }
        GRADERING_AVSLAG_ÅRSAK_MAP_MAP = Collections.unmodifiableMap(map);
    }

    private String verdi;

    GraderingAvslagÅrsak(String verdi) {
        this.verdi = verdi;
    }

    public static GraderingAvslagÅrsak get(String verdi) {
        return Optional.ofNullable(verdi)
                .map(GRADERING_AVSLAG_ÅRSAK_MAP_MAP::get)
                .orElse(UKJENT);
    }

    public String getVerdi() {
        return verdi;
    }
}
