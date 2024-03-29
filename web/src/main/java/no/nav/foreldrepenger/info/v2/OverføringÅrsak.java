package no.nav.foreldrepenger.info.v2;

enum OverføringÅrsak {
    INSTITUSJONSOPPHOLD_ANNEN_FORELDER,
    SYKDOM_ANNEN_FORELDER,
    IKKE_RETT_ANNEN_FORELDER,
    ALENEOMSORG,
    ;

    no.nav.foreldrepenger.common.innsyn.OverføringÅrsak tilDto() {
        return switch (this) {
            case INSTITUSJONSOPPHOLD_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.OverføringÅrsak.INSTITUSJONSOPPHOLD_ANNEN_FORELDER;
            case SYKDOM_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.OverføringÅrsak.SYKDOM_ANNEN_FORELDER;
            case IKKE_RETT_ANNEN_FORELDER -> no.nav.foreldrepenger.common.innsyn.OverføringÅrsak.IKKE_RETT_ANNEN_FORELDER;
            case ALENEOMSORG -> no.nav.foreldrepenger.common.innsyn.OverføringÅrsak.ALENEOMSORG;
        };
    }
}
