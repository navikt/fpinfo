package no.nav.foreldrepenger.info.v2;

import java.util.List;

record FpVedtak(List<UttakPeriode> perioder) {
    no.nav.foreldrepenger.common.innsyn.v2.FpVedtak tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.FpVedtak(perioder.stream().map(UttakPeriode::tilDto).toList());
    }
}
