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

    no.nav.foreldrepenger.common.innsyn.MorsAktivitet tilDto() {
        return switch (this) {
            case ARBEID -> no.nav.foreldrepenger.common.innsyn.MorsAktivitet.ARBEID;
            case UTDANNING -> no.nav.foreldrepenger.common.innsyn.MorsAktivitet.UTDANNING;
            case KVALPROG -> no.nav.foreldrepenger.common.innsyn.MorsAktivitet.KVALPROG;
            case INTROPROG -> no.nav.foreldrepenger.common.innsyn.MorsAktivitet.INTROPROG;
            case TRENGER_HJELP -> no.nav.foreldrepenger.common.innsyn.MorsAktivitet.TRENGER_HJELP;
            case INNLAGT -> no.nav.foreldrepenger.common.innsyn.MorsAktivitet.INNLAGT;
            case ARBEID_OG_UTDANNING -> no.nav.foreldrepenger.common.innsyn.MorsAktivitet.ARBEID_OG_UTDANNING;
            case UFØRE -> no.nav.foreldrepenger.common.innsyn.MorsAktivitet.UFØRE;
            case IKKE_OPPGITT -> no.nav.foreldrepenger.common.innsyn.MorsAktivitet.IKKE_OPPGITT;
        };
    }
}
