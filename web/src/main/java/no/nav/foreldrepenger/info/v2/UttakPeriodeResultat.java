package no.nav.foreldrepenger.info.v2;

record UttakPeriodeResultat(boolean innvilget, boolean trekkerMinsterett) {
    no.nav.foreldrepenger.common.innsyn.v2.UttakPeriodeResultat tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.UttakPeriodeResultat(innvilget, trekkerMinsterett);
    }
}
