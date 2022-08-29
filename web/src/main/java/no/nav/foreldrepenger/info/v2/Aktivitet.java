package no.nav.foreldrepenger.info.v2;

record Aktivitet(Type type, Arbeidsgiver arbeidsgiver) {

    enum Type {
        FRILANS, ORDINÆRT_ARBEID, SELVSTENDIG_NÆRINGSDRIVENDE, ANNET
    }
}
