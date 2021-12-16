package no.nav.foreldrepenger.info.v2;

record VedtakPeriodeResultat(boolean innvilget) {
    no.nav.foreldrepenger.info.v2.dto.VedtakPeriodeResultat tilDto() {
        return new no.nav.foreldrepenger.info.v2.dto.VedtakPeriodeResultat(innvilget);
    }
}
