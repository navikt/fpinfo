package no.nav.foreldrepenger.info.v2;

record UttakPeriodeResultat(boolean innvilget, boolean trekkerMinsterett, boolean trekkerDager, Årsak årsak) {
    no.nav.foreldrepenger.common.innsyn.v2.UttakPeriodeResultat tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.UttakPeriodeResultat(innvilget, trekkerMinsterett, trekkerDager, årsak.tilDto());
    }

    enum Årsak {
        ANNET, AVSLAG_HULL_MELLOM_FORELDRENES_PERIODER,
        ;

        public no.nav.foreldrepenger.common.innsyn.v2.UttakPeriodeResultat.Årsak tilDto() {
            return switch (this) {
                case ANNET -> no.nav.foreldrepenger.common.innsyn.v2.UttakPeriodeResultat.Årsak.ANNET;
                case AVSLAG_HULL_MELLOM_FORELDRENES_PERIODER -> no.nav.foreldrepenger.common.innsyn.v2.UttakPeriodeResultat.Årsak.AVSLAG_HULL_MELLOM_FORELDRENES_PERIODER;
            };
        }
    }
}
