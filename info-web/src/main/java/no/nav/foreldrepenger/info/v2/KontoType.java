package no.nav.foreldrepenger.info.v2;

enum KontoType {
    MØDREKVOTE, FEDREKVOTE, FELLESPERIODE, FORELDREPENGER, FORELDREPENGER_FØR_FØDSEL;

    no.nav.foreldrepenger.info.v2.dto.KontoType tilDto() {
        return switch (this) {
            case MØDREKVOTE -> no.nav.foreldrepenger.info.v2.dto.KontoType.MØDREKVOTE;
            case FEDREKVOTE -> no.nav.foreldrepenger.info.v2.dto.KontoType.FEDREKVOTE;
            case FELLESPERIODE -> no.nav.foreldrepenger.info.v2.dto.KontoType.FELLESPERIODE;
            case FORELDREPENGER -> no.nav.foreldrepenger.info.v2.dto.KontoType.FORELDREPENGER;
            case FORELDREPENGER_FØR_FØDSEL -> no.nav.foreldrepenger.info.v2.dto.KontoType.FORELDREPENGER_FØR_FØDSEL;
        };
    }
}
