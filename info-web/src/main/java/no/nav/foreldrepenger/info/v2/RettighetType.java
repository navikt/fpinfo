package no.nav.foreldrepenger.info.v2;

enum RettighetType {
    ALENEOMSORG, BEGGE_RETT, BARE_SØKER_RETT;

    no.nav.foreldrepenger.info.v2.dto.RettighetType tilDto() {
        return switch (this) {
            case ALENEOMSORG -> no.nav.foreldrepenger.info.v2.dto.RettighetType.ALENEOMSORG;
            case BEGGE_RETT -> no.nav.foreldrepenger.info.v2.dto.RettighetType.BEGGE_RETT;
            case BARE_SØKER_RETT -> no.nav.foreldrepenger.info.v2.dto.RettighetType.BARE_SØKER_RETT;
        };
    }
}
