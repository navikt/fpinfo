package no.nav.foreldrepenger.info.v2;

enum OppholdÅrsak {
    MØDREKVOTE_ANNEN_FORELDER,
    FEDREKVOTE_ANNEN_FORELDER,
    FELLESPERIODE_ANNEN_FORELDER,
    FORELDREPENGER_ANNEN_FORELDER,
    ;

    no.nav.foreldrepenger.common.innsyn.v2.OppholdÅrsak tilDto() {
        return switch (this) {
            case MØDREKVOTE_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.v2.OppholdÅrsak.MØDREKVOTE_ANNEN_FORELDER;
            case FEDREKVOTE_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.v2.OppholdÅrsak.FEDREKVOTE_ANNEN_FORELDER;
            case FELLESPERIODE_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.v2.OppholdÅrsak.FELLESPERIODE_ANNEN_FORELDER;
            case FORELDREPENGER_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.v2.OppholdÅrsak.FORELDREPENGER_ANNEN_FORELDER;
        };
    }
}
