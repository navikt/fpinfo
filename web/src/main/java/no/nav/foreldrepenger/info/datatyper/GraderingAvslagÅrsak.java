package no.nav.foreldrepenger.info.datatyper;

import java.util.Objects;
import java.util.stream.Stream;

public enum GraderingAvslagÅrsak {
    UKJENT("-"),
    GRADERING_FØR_UKE_7("4504"),
    FOR_SEN_SØKNAD("4501"),
    MANGLENDE_GRADERINGSAVTALE("4502"),
    MOR_OPPFYLLER_IKKE_AKTIVITETSKRAV("4503"),
    AVSLAG_PGA_100_PROSENT_ARBEID("4523");

    private final String verdi;

    GraderingAvslagÅrsak(String verdi) {
        this.verdi = verdi;
    }

    public static GraderingAvslagÅrsak get(String verdi) {
        return Stream.of(values()).filter(ev -> Objects.equals(ev.getVerdi(), verdi)).findFirst().orElse(UKJENT);
    }

    public String getVerdi() {
        return verdi;
    }
}
