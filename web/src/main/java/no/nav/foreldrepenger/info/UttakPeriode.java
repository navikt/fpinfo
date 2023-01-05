package no.nav.foreldrepenger.info;

import static no.nav.foreldrepenger.info.app.tjenester.dto.UttaksPeriodeDto.dash2null;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.converters.StringToGraderingAvslagÅrsakConverter;
import no.nav.foreldrepenger.info.converters.StringToMorsAktivitetConverter;
import no.nav.foreldrepenger.info.datatyper.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.info.datatyper.MorsAktivitet;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "UttakPeriode")
@Table(name = "UTTAK_PERIODE")
@Immutable
public class UttakPeriode {

    @Id
    @Column(name = "RANDOM_ID")
    private Long id;

    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "PERIODE_RESULTAT_TYPE")
    private String periodeResultatType;

    @Convert(converter = StringToGraderingAvslagÅrsakConverter.class)
    @Column(name = "GRADERING_AVSLAG_AARSAK")
    private GraderingAvslagÅrsak graderingAvslagAarsak;

    @Column(name = "PERIODE_RESULTAT_AARSAK")
    private String periodeResultatÅrsak;

    @Column(name = "UTTAK_UTSETTELSE_TYPE")
    private String uttakUtsettelseType;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "FLERBARNSDAGER")
    private Boolean flerbarnsdager;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "GRADERING_INNVILGET")
    private Boolean graderingInnvilget;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "MANUELT_BEHANDLET")
    private Boolean manueltBehandlet;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SAMTIDIG_UTTAK")
    private Boolean samtidigUttak;

    @Column(name = "FOM")
    private LocalDate fom;

    @Column(name = "TOM")
    private LocalDate tom;

    @Column(name = "TREKKONTO")
    private String trekkonto;

    @Column(name = "TREKKDAGER")
    private BigDecimal trekkdager;

    @Column(name = "ARBEIDSTIDSPROSENT")
    private BigDecimal arbeidstidprosent;

    @Column(name = "UTBETALINGSPROSENT")
    private BigDecimal utbetalingsprosent;

    @Column(name = "SAMTIDIG_UTTAKSPROSENT")
    private BigDecimal samtidigUttaksprosent;

    @Column(name = "OPPHOLD_AARSAK")
    private String oppholdÅrsak;

    @Column(name = "OVERFOERING_AARSAK")
    private String overføringÅrsak;

    @Column(name = "UTTAK_ARBEID_TYPE")
    private String uttakArbeidType;

    @Column(name = "ARBEIDSGIVER_AKTOR_ID")
    private String arbeidsgiverAktørId;

    @Column(name = "ARBEIDSGIVER_ORGNR")
    private String arbeidsgiverOrgnr;

    @Convert(converter = StringToMorsAktivitetConverter.class)
    @Column(name = "MORS_AKTIVITET")
    private MorsAktivitet morsAktivitet;

    public String getPeriodeResultatType() {
        return periodeResultatType;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public String getGraderingAvslagAarsak() {
        return graderingAvslagAarsak == null || graderingAvslagAarsak.equals(GraderingAvslagÅrsak.UKJENT) ? null : graderingAvslagAarsak.name();
    }

    public String getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak;
    }

    public String getUttakUtsettelseType() {
        return uttakUtsettelseType;
    }

    public boolean getFlerbarnsdager() {
        return Boolean.TRUE.equals(flerbarnsdager);
    }

    public boolean getGraderingInnvilget() {
        return Boolean.TRUE.equals(graderingInnvilget);
    }

    public Boolean getManueltBehandlet() {
        return manueltBehandlet;
    }

    public boolean getSamtidigUttak() {
        return (samtidigUttak != null) && samtidigUttak;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public String getTrekkonto() {
        return dash2null(trekkonto);
    }

    public BigDecimal getTrekkdager() {
        return trekkdager;
    }

    public Prosent getArbeidstidprosent() {
        return arbeidstidprosent == null ? null : new Prosent(arbeidstidprosent);
    }

    public Prosent getUtbetalingsprosent() {
        return utbetalingsprosent == null ? null : new Prosent(utbetalingsprosent);
    }

    public Prosent getSamtidigUttaksprosent() {
        return samtidigUttaksprosent == null ? null : new Prosent(samtidigUttaksprosent);
    }

    public String getOppholdÅrsak() {
        return oppholdÅrsak;
    }

    public String getOverføringÅrsak() {
        return overføringÅrsak;
    }

    public String getUttakArbeidType() {
        return uttakArbeidType;
    }

    public String getArbeidsgiverAktørId() {
        return arbeidsgiverAktørId;
    }

    public String getArbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public MorsAktivitet getMorsAktivitet() {
        return morsAktivitet;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [id=" + id + ", behandlingId=" + behandlingId + ", periodeResultatType=" + periodeResultatType
                + ", graderingAvslagAarsak=" + graderingAvslagAarsak + ", periodeResultatÅrsak=" + periodeResultatÅrsak
                + ", uttakUtsettelseType=" + uttakUtsettelseType + ", flerbarnsdager=" + flerbarnsdager + ", graderingInnvilget="
                + graderingInnvilget + ", manueltBehandlet=" + manueltBehandlet + ", samtidigUttak=" + samtidigUttak + ", fom=" + fom
                + ", tom=" + tom + ", trekkonto=" + trekkonto + ", trekkdager=" + trekkdager + ", arbeidstidprosent=" + arbeidstidprosent
                + ", utbetalingsprosent=" + utbetalingsprosent + ", samtidigUttaksprosent=" + samtidigUttaksprosent + ", oppholdÅrsak="
                + oppholdÅrsak + ", overføringÅrsak=" + overføringÅrsak + ", uttakArbeidType=" + uttakArbeidType + ", arbeidsgiverAktørId="
                + arbeidsgiverAktørId + ", arbeidsgiverOrgnr=" + arbeidsgiverOrgnr + ", morsAktivitet=" + morsAktivitet + "]";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UttakPeriode uttakPeriode;

        public Builder() {
            uttakPeriode = new UttakPeriode();
        }

        public Builder withId(Long id) {
            uttakPeriode.id = id;
            return this;
        }

        public Builder withBehandlingId(Long behandlingId) {
            uttakPeriode.behandlingId = behandlingId;
            return this;
        }

        public Builder withPeriodeResultatType(String periodeResultatType) {
            uttakPeriode.periodeResultatType = periodeResultatType;
            return this;
        }

        public Builder withGraderingAvslagAarsak(GraderingAvslagÅrsak graderingAvslagAarsak) {
            uttakPeriode.graderingAvslagAarsak = graderingAvslagAarsak;
            return this;
        }

        public Builder withPeriodeResultatÅrsak(String periodeResultatÅrsak) {
            uttakPeriode.periodeResultatÅrsak = periodeResultatÅrsak;
            return this;
        }

        public Builder withUttakUtsettelseType(String uttakUtsettelseType) {
            uttakPeriode.uttakUtsettelseType = uttakUtsettelseType;
            return this;
        }

        public Builder withFlerbarnsdager(Boolean flerbarnsdager) {
            uttakPeriode.flerbarnsdager = flerbarnsdager;
            return this;
        }

        public Builder withGraderingInnvilget(Boolean graderingInnvilget) {
            uttakPeriode.graderingInnvilget = graderingInnvilget;
            return this;
        }

        public Builder withManueltBehandlet(Boolean manueltBehandlet) {
            uttakPeriode.manueltBehandlet = manueltBehandlet;
            return this;
        }

        public Builder withSamtidigUttak(Boolean samtidigUttak) {
            uttakPeriode.samtidigUttak = samtidigUttak;
            return this;
        }

        public Builder withFom(LocalDate fom) {
            uttakPeriode.fom = fom;
            return this;
        }

        public Builder withTom(LocalDate tom) {
            uttakPeriode.tom = tom;
            return this;
        }

        public Builder withTrekkkonto(String trekkkonto) {
            uttakPeriode.trekkonto = trekkkonto;
            return this;
        }

        public Builder withTrekkdager(BigDecimal trekkdager) {
            uttakPeriode.trekkdager = trekkdager;
            return this;
        }

        public Builder withArbeidstidprosent(BigDecimal arbeidstidprosent) {
            uttakPeriode.arbeidstidprosent = arbeidstidprosent;
            return this;
        }

        public Builder withUtbetalingsprosent(BigDecimal utbetalingsprosent) {
            uttakPeriode.utbetalingsprosent = utbetalingsprosent;
            return this;
        }

        public Builder withSamtidigUttaksprosent(BigDecimal samtidigUttaksprosent) {
            uttakPeriode.samtidigUttaksprosent = samtidigUttaksprosent;
            return this;
        }

        public Builder withOppholdÅrsak(String oppholdÅrsak) {
            uttakPeriode.oppholdÅrsak = oppholdÅrsak;
            return this;
        }

        public Builder withOverføringÅrsak(String overføringÅrsak) {
            uttakPeriode.overføringÅrsak = overføringÅrsak;
            return this;
        }

        public Builder withUttakArbeidType(String uttakArbeidType) {
            uttakPeriode.uttakArbeidType = uttakArbeidType;
            return this;
        }

        public Builder withArbeidsgiverAktørId(String arbeidsgiverAktørId) {
            uttakPeriode.arbeidsgiverAktørId = arbeidsgiverAktørId;
            return this;
        }

        public Builder withArbeidsgiverOrgnr(String arbeidsgiverOrgnr) {
            uttakPeriode.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
            return this;
        }

        public Builder withMorsAktivitet(MorsAktivitet morsAktivitet) {
            uttakPeriode.morsAktivitet = morsAktivitet;
            return this;
        }

        public UttakPeriode build() {
            return uttakPeriode;
        }
    }
}
