package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;
import java.util.List;

record AnnenPartVedtak(List<VedtakPeriode> perioder,
                       LocalDate termindato,
                       Dekningsgrad dekningsgrad) {
}
