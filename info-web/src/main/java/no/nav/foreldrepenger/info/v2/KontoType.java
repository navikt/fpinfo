package no.nav.foreldrepenger.info.v2;

enum KontoType {

    MØDREKVOTE, FEDREKVOTE, FELLESPERIODE, FORELDREPENGER, FORELDREPENGER_FØR_FØDSEL;

    static KontoType fraString(String value) {
        if (value == null || value.isEmpty() || value.equals("-")) {
            return null;
        }
        return KontoType.valueOf(value);
    }
}