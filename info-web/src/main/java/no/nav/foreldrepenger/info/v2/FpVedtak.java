package no.nav.foreldrepenger.info.v2;

import java.util.List;

record FpVedtak(List<VedtakPeriode> perioder) {
    no.nav.foreldrepenger.info.v2.dto.FpVedtak tilDto() {
        return new no.nav.foreldrepenger.info.v2.dto.FpVedtak(perioder.stream().map(p -> p.tilDto()).toList());
    }
}
