package no.nav.foreldrepenger.info.v2;

enum OppholdÅrsak {
    MØDREKVOTE_ANNEN_FORELDER,
    FEDREKVOTE_ANNEN_FORELDER,
    FELLESPERIODE_ANNEN_FORELDER,
    FORELDREPENGER_ANNEN_FORELDER,
    ;

    no.nav.foreldrepenger.common.innsyn.OppholdÅrsak tilDto() {
        return switch (this) {
            case MØDREKVOTE_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.OppholdÅrsak.MØDREKVOTE_ANNEN_FORELDER;
            case FEDREKVOTE_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.OppholdÅrsak.FEDREKVOTE_ANNEN_FORELDER;
            case FELLESPERIODE_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.OppholdÅrsak.FELLESPERIODE_ANNEN_FORELDER;
            case FORELDREPENGER_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.OppholdÅrsak.FORELDREPENGER_ANNEN_FORELDER;
        };
    }
}
