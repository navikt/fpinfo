package no.nav.foreldrepenger.info.domene;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.domene.converters.StringToGraderingAvslagÅrsakConverter;
import no.nav.foreldrepenger.info.domene.converters.StringToMorsAktivitetConverter;
import no.nav.foreldrepenger.info.felles.datatyper.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.info.felles.datatyper.MorsAktivitet;
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
    private Long arbeidstidprosent;

    @Column(name = "UTBETALINGSPROSENT")
    private Long utbetalingsprosent;

    @Column(name = "SAMTIDIG_UTTAKSPROSENT")
    private Long samtidigUttaksprosent;

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
        return graderingAvslagAarsak.equals(GraderingAvslagÅrsak.UKJENT) ? null : graderingAvslagAarsak.name();
    }

    public String getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak;
    }

    public String getUttakUtsettelseType() {
        return uttakUtsettelseType;
    }

    public Boolean getFlerbarnsdager() {
        return flerbarnsdager;
    }

    public Boolean getGraderingInnvilget() {
        return graderingInnvilget;
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
        return trekkonto;
    }

    public BigDecimal getTrekkdager() {
        return trekkdager;
    }

    public Long getArbeidstidprosent() {
        return arbeidstidprosent;
    }

    public Long getUtbetalingsprosent() {
        return utbetalingsprosent;
    }

    public Long getSamtidigUttaksprosent() {
        return samtidigUttaksprosent;
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

    public String getMorsAktivitet() {
        return morsAktivitet.getVerdi();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UttakPeriode uttakPeriode;

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

        public Builder withArbeidstidprosent(Long arbeidstidprosent) {
            uttakPeriode.arbeidstidprosent = arbeidstidprosent;
            return this;
        }

        public Builder withUtbetalingsprosent(Long utbetalingsprosent) {
            uttakPeriode.utbetalingsprosent = utbetalingsprosent;
            return this;
        }

        public Builder withSamtidigUttaksprosent(Long samtidigUttaksprosent) {
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
