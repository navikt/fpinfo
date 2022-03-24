package no.nav.foreldrepenger.info.v2;

enum Dekningsgrad {
    ÅTTI, HUNDRE;

    static Dekningsgrad valueOf(Integer value) {
        if (value == null) {
            return null;
        }
        if (value == 80) {
            return ÅTTI;
        }
        if (value == 100) {
            return HUNDRE;
        }
        throw new IllegalArgumentException("Ukjent dekningsgrad " + value);
    }

    no.nav.foreldrepenger.common.innsyn.v2.Dekningsgrad tilDto() {
        return switch (this) {
            case ÅTTI -> no.nav.foreldrepenger.common.innsyn.v2.Dekningsgrad.ÅTTI;
            case HUNDRE -> no.nav.foreldrepenger.common.innsyn.v2.Dekningsgrad.HUNDRE;
        };
    }
}
