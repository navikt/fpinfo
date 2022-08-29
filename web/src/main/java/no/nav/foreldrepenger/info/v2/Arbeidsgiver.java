package no.nav.foreldrepenger.info.v2;


import static no.nav.foreldrepenger.common.util.StringUtil.mask;

record Arbeidsgiver(String id, ArbeidsgiverType type) {

    enum ArbeidsgiverType {
        PRIVAT,
        ORGANISASJON
    }

    @Override
    public String toString() {
        return "Arbeidsgiver{" + "id='" + mask(id) + '\'' + ", type=" + type + '}';
    }
}
