package no.nav.foreldrepenger.info.v2;

enum MorsAktivitet {
    ARBEID,
    UTDANNING,
    SAMTIDIGUTTAK,
    KVALPROG,
    INTROPROG,
    TRENGER_HJELP,
    INNLAGT,
    ARBEID_OG_UTDANNING,
    UFØRE;

    no.nav.foreldrepenger.info.v2.dto.MorsAktivitet tilDto() {
        return switch (this) {
            case ARBEID -> no.nav.foreldrepenger.info.v2.dto.MorsAktivitet.ARBEID;
            case UTDANNING -> no.nav.foreldrepenger.info.v2.dto.MorsAktivitet.UTDANNING;
            case SAMTIDIGUTTAK -> no.nav.foreldrepenger.info.v2.dto.MorsAktivitet.SAMTIDIGUTTAK;
            case KVALPROG -> no.nav.foreldrepenger.info.v2.dto.MorsAktivitet.KVALPROG;
            case INTROPROG -> no.nav.foreldrepenger.info.v2.dto.MorsAktivitet.INTROPROG;
            case TRENGER_HJELP -> no.nav.foreldrepenger.info.v2.dto.MorsAktivitet.TRENGER_HJELP;
            case INNLAGT -> no.nav.foreldrepenger.info.v2.dto.MorsAktivitet.INNLAGT;
            case ARBEID_OG_UTDANNING -> no.nav.foreldrepenger.info.v2.dto.MorsAktivitet.ARBEID_OG_UTDANNING;
            case UFØRE -> no.nav.foreldrepenger.info.v2.dto.MorsAktivitet.UFØRE;
        };
    }
}
