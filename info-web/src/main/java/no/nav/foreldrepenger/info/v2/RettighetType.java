package no.nav.foreldrepenger.info.v2;

enum RettighetType {
    ALENEOMSORG, BEGGE_RETT, BARE_SØKER_RETT;

    no.nav.foreldrepenger.common.innsyn.v2.RettighetType tilDto() {
        return switch (this) {
            case ALENEOMSORG -> no.nav.foreldrepenger.common.innsyn.v2.RettighetType.ALENEOMSORG;
            case BEGGE_RETT -> no.nav.foreldrepenger.common.innsyn.v2.RettighetType.BEGGE_RETT;
            case BARE_SØKER_RETT -> no.nav.foreldrepenger.common.innsyn.v2.RettighetType.BARE_SØKER_RETT;
        };
    }
}
