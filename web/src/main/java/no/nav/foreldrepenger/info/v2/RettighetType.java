package no.nav.foreldrepenger.info.v2;

enum RettighetType {
    ALENEOMSORG, BEGGE_RETT, BARE_SØKER_RETT;

    no.nav.foreldrepenger.common.innsyn.RettighetType tilDto() {
        return switch (this) {
            case ALENEOMSORG -> no.nav.foreldrepenger.common.innsyn.RettighetType.ALENEOMSORG;
            case BEGGE_RETT -> no.nav.foreldrepenger.common.innsyn.RettighetType.BEGGE_RETT;
            case BARE_SØKER_RETT -> no.nav.foreldrepenger.common.innsyn.RettighetType.BARE_SØKER_RETT;
        };
    }
}
