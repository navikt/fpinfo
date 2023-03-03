package no.nav.foreldrepenger.info;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.converters.StringToMorsAktivitetConverter;
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

    public static String dash2null(String value) {
        return Objects.equals(value, "-") ? null : value;
    }

    public BigDecimal getTrekkdager() {
        return trekkdager;
    }

    public Prosent getArbeidstidprosent() {
        return arbeidstidprosent == null ? null : new Prosent(arbeidstidprosent);
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
        return "UttakPeriode{" + "id=" + id + ", behandlingId=" + behandlingId + ", periodeResultatType='" + periodeResultatType + '\''
            + ", periodeResultatÅrsak='" + periodeResultatÅrsak + '\'' + ", uttakUtsettelseType='" + uttakUtsettelseType + '\'' + ", flerbarnsdager="
            + flerbarnsdager + ", graderingInnvilget=" + graderingInnvilget + ", samtidigUttak=" + samtidigUttak + ", fom=" + fom + ", tom=" + tom
            + ", trekkonto='" + trekkonto + '\'' + ", trekkdager=" + trekkdager + ", arbeidstidprosent=" + arbeidstidprosent
            + ", samtidigUttaksprosent=" + samtidigUttaksprosent + ", oppholdÅrsak='" + oppholdÅrsak + '\'' + ", overføringÅrsak='" + overføringÅrsak
            + '\'' + ", uttakArbeidType='" + uttakArbeidType + '\'' + ", morsAktivitet=" + morsAktivitet + '}';
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

        public Builder behandlingId(Long behandlingId) {
            uttakPeriode.behandlingId = behandlingId;
            return this;
        }

        public Builder periodeResultatType(String periodeResultatType) {
            uttakPeriode.periodeResultatType = periodeResultatType;
            return this;
        }

        public Builder periodeResultatÅrsak(String periodeResultatÅrsak) {
            uttakPeriode.periodeResultatÅrsak = periodeResultatÅrsak;
            return this;
        }

        public Builder uttakUtsettelseType(String uttakUtsettelseType) {
            uttakPeriode.uttakUtsettelseType = uttakUtsettelseType;
            return this;
        }

        public Builder flerbarnsdager(Boolean flerbarnsdager) {
            uttakPeriode.flerbarnsdager = flerbarnsdager;
            return this;
        }

        public Builder graderingInnvilget(Boolean graderingInnvilget) {
            uttakPeriode.graderingInnvilget = graderingInnvilget;
            return this;
        }

        public Builder samtidigUttak(Boolean samtidigUttak) {
            uttakPeriode.samtidigUttak = samtidigUttak;
            return this;
        }

        public Builder fom(LocalDate fom) {
            uttakPeriode.fom = fom;
            return this;
        }

        public Builder tom(LocalDate tom) {
            uttakPeriode.tom = tom;
            return this;
        }

        public Builder trekkkonto(String trekkkonto) {
            uttakPeriode.trekkonto = trekkkonto;
            return this;
        }

        public Builder trekkdager(BigDecimal trekkdager) {
            uttakPeriode.trekkdager = trekkdager;
            return this;
        }

        public Builder arbeidstidprosent(BigDecimal arbeidstidprosent) {
            uttakPeriode.arbeidstidprosent = arbeidstidprosent;
            return this;
        }

        public Builder samtidigUttaksprosent(BigDecimal samtidigUttaksprosent) {
            uttakPeriode.samtidigUttaksprosent = samtidigUttaksprosent;
            return this;
        }

        public Builder oppholdÅrsak(String oppholdÅrsak) {
            uttakPeriode.oppholdÅrsak = oppholdÅrsak;
            return this;
        }

        public Builder overføringÅrsak(String overføringÅrsak) {
            uttakPeriode.overføringÅrsak = overføringÅrsak;
            return this;
        }

        public Builder uttakArbeidType(String uttakArbeidType) {
            uttakPeriode.uttakArbeidType = uttakArbeidType;
            return this;
        }

        public Builder arbeidsgiverAktørId(String arbeidsgiverAktørId) {
            uttakPeriode.arbeidsgiverAktørId = arbeidsgiverAktørId;
            return this;
        }

        public Builder arbeidsgiverOrgnr(String arbeidsgiverOrgnr) {
            uttakPeriode.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
            return this;
        }

        public Builder morsAktivitet(MorsAktivitet morsAktivitet) {
            uttakPeriode.morsAktivitet = morsAktivitet;
            return this;
        }

        public UttakPeriode build() {
            return uttakPeriode;
        }
    }
}
