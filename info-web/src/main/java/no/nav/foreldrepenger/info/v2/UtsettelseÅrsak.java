package no.nav.foreldrepenger.info.v2;

enum UtsettelseÅrsak{
    HV_ØVELSE,
    ARBEID,
    LOVBESTEMT_FERIE,
    SØKER_SYKDOM,
    SØKER_INNLAGT,
    BARN_INNLAGT,
    NAV_TILTAK,
    FRI;

    no.nav.foreldrepenger.info.v2.dto.UtsettelseÅrsak tilDto() {
        return switch (this) {
            case HV_ØVELSE -> no.nav.foreldrepenger.info.v2.dto.UtsettelseÅrsak.HV_ØVELSE;
            case ARBEID -> no.nav.foreldrepenger.info.v2.dto.UtsettelseÅrsak.ARBEID;
            case LOVBESTEMT_FERIE -> no.nav.foreldrepenger.info.v2.dto.UtsettelseÅrsak.LOVBESTEMT_FERIE;
            case SØKER_SYKDOM -> no.nav.foreldrepenger.info.v2.dto.UtsettelseÅrsak.SØKER_SYKDOM;
            case SØKER_INNLAGT -> no.nav.foreldrepenger.info.v2.dto.UtsettelseÅrsak.SØKER_INNLAGT;
            case BARN_INNLAGT -> no.nav.foreldrepenger.info.v2.dto.UtsettelseÅrsak.BARN_INNLAGT;
            case NAV_TILTAK -> no.nav.foreldrepenger.info.v2.dto.UtsettelseÅrsak.NAV_TILTAK;
            case FRI -> no.nav.foreldrepenger.info.v2.dto.UtsettelseÅrsak.FRI;
        };
    }
}
