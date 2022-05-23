package no.nav.foreldrepenger.info.v2;

enum MorsAktivitet {
    ARBEID,
    UTDANNING,
    KVALPROG,
    INTROPROG,
    TRENGER_HJELP,
    INNLAGT,
    ARBEID_OG_UTDANNING,
    UFØRE,
    IKKE_OPPGITT;

    no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet tilDto() {
        return switch (this) {
            case ARBEID -> no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet.ARBEID;
            case UTDANNING -> no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet.UTDANNING;
            case KVALPROG -> no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet.KVALPROG;
            case INTROPROG -> no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet.INTROPROG;
            case TRENGER_HJELP -> no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet.TRENGER_HJELP;
            case INNLAGT -> no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet.INNLAGT;
            case ARBEID_OG_UTDANNING -> no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet.ARBEID_OG_UTDANNING;
            case UFØRE -> no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet.UFØRE;
            case IKKE_OPPGITT -> no.nav.foreldrepenger.common.innsyn.v2.MorsAktivitet.IKKE_OPPGITT;
        };
    }
}
