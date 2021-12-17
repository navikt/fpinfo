package no.nav.foreldrepenger.info.v2;

enum OppholdÅrsak {
    MØDREKVOTE_ANNEN_FORELDER,
    FEDREKVOTE_ANNEN_FORELDER,
    FELLESPERIODE_ANNEN_FORELDER,
    FORELDREPENGER_ANNEN_FORELDER,
    ;

    no.nav.foreldrepenger.info.v2.dto.OppholdÅrsak tilDto() {
        return switch (this) {
            case MØDREKVOTE_ANNEN_FORELDER -> no.nav.foreldrepenger.info.v2.dto.OppholdÅrsak.MØDREKVOTE_ANNEN_FORELDER;
            case FEDREKVOTE_ANNEN_FORELDER -> no.nav.foreldrepenger.info.v2.dto.OppholdÅrsak.FEDREKVOTE_ANNEN_FORELDER;
            case FELLESPERIODE_ANNEN_FORELDER -> no.nav.foreldrepenger.info.v2.dto.OppholdÅrsak.FELLESPERIODE_ANNEN_FORELDER;
            case FORELDREPENGER_ANNEN_FORELDER -> no.nav.foreldrepenger.info.v2.dto.OppholdÅrsak.FORELDREPENGER_ANNEN_FORELDER;
        };
    }
}
