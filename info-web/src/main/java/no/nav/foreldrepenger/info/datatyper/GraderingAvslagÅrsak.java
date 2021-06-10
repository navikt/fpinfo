package no.nav.foreldrepenger.info.datatyper;

import static no.nav.vedtak.util.EnumUtil.match;

import java.util.Optional;
import java.util.function.Predicate;

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
        return Optional.ofNullable(verdi)
                .map(v -> match(GraderingAvslagÅrsak.class, (Predicate<GraderingAvslagÅrsak>) (e) -> e.getVerdi().equals(v),
                        UKJENT))
                .orElse(UKJENT);
    }

    public String getVerdi() {
        return verdi;
    }
}
