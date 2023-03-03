package no.nav.foreldrepenger.info.v2;

import java.util.List;

record FpVedtak(List<UttakPeriode> perioder) {
    no.nav.foreldrepenger.common.innsyn.FpVedtak tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.FpVedtak(perioder.stream().map(UttakPeriode::tilDto).toList());
    }
}
