package no.nav.foreldrepenger.info.datatyper;

import static no.nav.foreldrepenger.info.datatyper.GraderingAvslagÅrsak.MANGLENDE_GRADERINGSAVTALE;
import static no.nav.foreldrepenger.info.datatyper.GraderingAvslagÅrsak.UKJENT;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EnumTest {

    @Test
    void test() {
        assertThat(GraderingAvslagÅrsak.get("4502")).isEqualTo(MANGLENDE_GRADERINGSAVTALE);
        assertThat(GraderingAvslagÅrsak.get("xxx")).isEqualTo(UKJENT);
        assertThat(GraderingAvslagÅrsak.get(null)).isEqualTo(UKJENT);
    }
}
