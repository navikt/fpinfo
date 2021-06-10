package no.nav.foreldrepenger.info.datatyper;

import static no.nav.foreldrepenger.info.datatyper.GraderingAvslagÅrsak.MANGLENDE_GRADERINGSAVTALE;
import static no.nav.foreldrepenger.info.datatyper.GraderingAvslagÅrsak.UKJENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EnumTest {

    @Test
    void test() {
        assertEquals(MANGLENDE_GRADERINGSAVTALE, GraderingAvslagÅrsak.get("4502"));
        assertEquals(UKJENT, GraderingAvslagÅrsak.get("xxx"));
        assertEquals(UKJENT, GraderingAvslagÅrsak.get(null));
    }
}
