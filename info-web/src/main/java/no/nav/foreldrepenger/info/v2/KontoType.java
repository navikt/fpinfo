package no.nav.foreldrepenger.info.v2;

enum KontoType {
    MØDREKVOTE, FEDREKVOTE, FELLESPERIODE, FORELDREPENGER, FORELDREPENGER_FØR_FØDSEL;

    no.nav.foreldrepenger.common.innsyn.v2.KontoType tilDto() {
        return switch (this) {
            case MØDREKVOTE -> no.nav.foreldrepenger.common.innsyn.v2.KontoType.MØDREKVOTE;
            case FEDREKVOTE -> no.nav.foreldrepenger.common.innsyn.v2.KontoType.FEDREKVOTE;
            case FELLESPERIODE -> no.nav.foreldrepenger.common.innsyn.v2.KontoType.FELLESPERIODE;
            case FORELDREPENGER -> no.nav.foreldrepenger.common.innsyn.v2.KontoType.FORELDREPENGER;
            case FORELDREPENGER_FØR_FØDSEL -> no.nav.foreldrepenger.common.innsyn.v2.KontoType.FORELDREPENGER_FØR_FØDSEL;
        };
    }
}
