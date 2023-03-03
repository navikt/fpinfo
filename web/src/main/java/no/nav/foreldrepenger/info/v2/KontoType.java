package no.nav.foreldrepenger.info.v2;

enum KontoType {
    MØDREKVOTE, FEDREKVOTE, FELLESPERIODE, FORELDREPENGER, FORELDREPENGER_FØR_FØDSEL;

    no.nav.foreldrepenger.common.innsyn.KontoType tilDto() {
        return switch (this) {
            case MØDREKVOTE -> no.nav.foreldrepenger.common.innsyn.KontoType.MØDREKVOTE;
            case FEDREKVOTE -> no.nav.foreldrepenger.common.innsyn.KontoType.FEDREKVOTE;
            case FELLESPERIODE -> no.nav.foreldrepenger.common.innsyn.KontoType.FELLESPERIODE;
            case FORELDREPENGER -> no.nav.foreldrepenger.common.innsyn.KontoType.FORELDREPENGER;
            case FORELDREPENGER_FØR_FØDSEL -> no.nav.foreldrepenger.common.innsyn.KontoType.FORELDREPENGER_FØR_FØDSEL;
        };
    }
}
