package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;
import java.util.List;

record AnnenPartVedtak(List<UttakPeriode> perioder,
                       LocalDate termindato,
                       Dekningsgrad dekningsgrad) {
}
