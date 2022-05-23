package no.nav.foreldrepenger.info.v2;

record VedtakPeriodeResultat(boolean innvilget) {
    no.nav.foreldrepenger.common.innsyn.v2.VedtakPeriodeResultat tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.VedtakPeriodeResultat(innvilget);
    }
}
