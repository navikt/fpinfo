package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;

record UttakPeriode(LocalDate fom,
                    LocalDate tom,
                    KontoType kontoType,
                    UttakPeriodeResultat resultat,
                    UtsettelseÅrsak utsettelseÅrsak,
                    OppholdÅrsak oppholdÅrsak,
                    OverføringÅrsak overføringÅrsak,
                    Gradering gradering,
                    MorsAktivitet morsAktivitet,
                    SamtidigUttak samtidigUttak,
                    boolean flerbarnsdager) {

    no.nav.foreldrepenger.common.innsyn.v2.UttakPeriode tilDto() {
        var kontoTypeDto = kontoType == null ? null : kontoType.tilDto();
        var utsettelseÅrsakDto = utsettelseÅrsak == null ? null : utsettelseÅrsak.tilDto();
        var oppholdÅrsakDto = oppholdÅrsak == null ? null : oppholdÅrsak.tilDto();
        var overføringÅrsakDto = overføringÅrsak == null ? null : overføringÅrsak.tilDto();
        var graderingDto = gradering == null ? null : gradering.tilDto();
        var morsAktivitetDto = morsAktivitet == null ? null : morsAktivitet.tilDto();
        var samtidigUttakDto = samtidigUttak == null ? null : samtidigUttak.tilDto();
        return new no.nav.foreldrepenger.common.innsyn.v2.UttakPeriode(fom, tom, kontoTypeDto,
                resultat == null ? null : resultat.tilDto(), utsettelseÅrsakDto, oppholdÅrsakDto,
                overføringÅrsakDto, graderingDto, morsAktivitetDto, samtidigUttakDto, flerbarnsdager);
    }
}
