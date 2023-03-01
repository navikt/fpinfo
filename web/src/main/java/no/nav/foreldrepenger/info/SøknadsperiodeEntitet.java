package no.nav.foreldrepenger.info;

import static no.nav.foreldrepenger.info.UttakPeriode.dash2null;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.common.innsyn.v2.Aktivitet;
import no.nav.foreldrepenger.info.converters.StringToMorsAktivitetConverter;
import no.nav.foreldrepenger.info.datatyper.MorsAktivitet;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "SoeknadPeriode")
@Table(name = "SOEKNAD_PERIODE")
@Immutable
public class SøknadsperiodeEntitet {

    private static final String UTSETTELSE_AARSAK_KODEVERK = "UTSETTELSE_AARSAK_TYPE";
    private static final String OVERFØRING_AARSAK_KODEVERK = "OVERFOERING_AARSAK_TYPE";
    private static final String OPPHOLD_AARSAK_KODEVERK = "OPPHOLD_AARSAK_TYPE";

    @Id
    @Column(name = "ID")
    private long id;

    @Column(name = "BEHANDLING_ID")
    private long behandlingId;

    @Column(name = "FOM")
    private LocalDate fom;

    @Column(name = "TOM")
    private LocalDate tom;

    @Column(name = "TREKKONTO")
    private String trekkonto;

    @Column(name = "ARBEIDSTIDPROSENT")
    private BigDecimal arbeidstidprosent;
    @Column(name = "SAMTIDIG_UTTAKSPROSENT")
    private BigDecimal samtidigUttaksprosent;

    @Column(name = "AARSAK")
    private String årsak;

    @Column(name = "AARSAK_KODEVERK")
    private String årsakKodeverk;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "ARBEIDSTAKER")
    private boolean arbeidstaker;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "FRILANSER")
    private boolean frilanser;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SELVSTENDIG")
    private boolean selvstendig;

    @Column(name = "ARBEIDSGIVER_ORGNR")
    private String arbeidsgiverOrgnr;

    @Column(name = "ARBEIDSGIVER_AKTOR_ID")
    private String arbeidsgiverAktørId;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "FLERBARNSDAGER")
    private boolean flerbarnsdager;

    @Convert(converter = StringToMorsAktivitetConverter.class)
    @Column(name = "MORS_AKTIVITET")
    private MorsAktivitet morsAktivitet;

    public long getId() {
        return id;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public Optional<String> getTrekkonto() {
        return Optional.ofNullable(dash2null(trekkonto));
    }

    public Prosent getArbeidstidprosent() {
        return arbeidstidprosent == null ? null : new Prosent(arbeidstidprosent);
    }

    public String getUtsettelseÅrsak() {
        return UTSETTELSE_AARSAK_KODEVERK.equals(årsakKodeverk) ? årsak : null;
    }

    public String getOverføringÅrsak() {
        return OVERFØRING_AARSAK_KODEVERK.equals(årsakKodeverk) ? årsak : null;
    }

    public String getOppholdÅrsak() {
        return OPPHOLD_AARSAK_KODEVERK.equals(årsakKodeverk) ? årsak : null;
    }

    public boolean isArbeidstaker() {
        return arbeidstaker;
    }

    public boolean isFrilanser() {
        return frilanser;
    }

    public boolean isSelvstendig() {
        return selvstendig;
    }

    public String getArbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public String getArbeidsgiverAktørId() {
        return arbeidsgiverAktørId;
    }

    public boolean isFlerbarnsdager() {
        return flerbarnsdager;
    }

    public MorsAktivitet getMorsAktivitet() {
        return morsAktivitet;
    }

    public Prosent getSamtidigUttaksprosent() {
        return samtidigUttaksprosent == null ? null : new Prosent(samtidigUttaksprosent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SøknadsperiodeEntitet that = (SøknadsperiodeEntitet) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private SøknadsperiodeEntitet kladd = new SøknadsperiodeEntitet();

        public Builder fom(LocalDate fom) {
            kladd.fom = fom;
            return this;
        }

        public Builder tom(LocalDate tom) {
            kladd.tom = tom;
            return this;
        }

        public Builder trekkonto(String trekkonto) {
            kladd.trekkonto = trekkonto;
            return this;
        }

        public Builder gradering(BigDecimal arbeidstidprosent, String arbeidsgiverOrgnr, String arbeidsgiverAktørId, Aktivitet.Type aktivitet) {
            kladd.arbeidstidprosent = arbeidstidprosent;
            kladd.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
            kladd.arbeidsgiverAktørId = arbeidsgiverAktørId;
            kladd.arbeidstaker = Aktivitet.Type.ORDINÆRT_ARBEID.equals(aktivitet);
            kladd.frilanser = Aktivitet.Type.FRILANS.equals(aktivitet);
            kladd.selvstendig = Aktivitet.Type.SELVSTENDIG_NÆRINGSDRIVENDE.equals(aktivitet);
            return this;
        }

        public Builder utsettelseÅrsak(String årsak) {
            kladd.årsak = årsak;
            kladd.årsakKodeverk = UTSETTELSE_AARSAK_KODEVERK;
            return this;
        }

        public Builder oppholdÅrsak(String årsak) {
            kladd.årsak = årsak;
            kladd.årsakKodeverk = OPPHOLD_AARSAK_KODEVERK;
            return this;
        }

        public Builder overføringÅrsak(String årsak) {
            kladd.årsak = årsak;
            kladd.årsakKodeverk = OVERFØRING_AARSAK_KODEVERK;
            return this;
        }

        public Builder arbeidsgiverOrgnr(String arbeidsgiverOrgnr) {
            kladd.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
            return this;
        }

        public Builder flerbarnsdager(boolean flerbarnsdager) {
            kladd.flerbarnsdager = flerbarnsdager;
            return this;
        }

        public Builder samtidigUttaksprosent(BigDecimal samtidigUttaksprosent) {
            kladd.samtidigUttaksprosent = samtidigUttaksprosent;
            return this;
        }

        public Builder morsAktivitet(MorsAktivitet morsAktivitet) {
            kladd.morsAktivitet = morsAktivitet;
            return this;
        }

        public SøknadsperiodeEntitet build() {
            var temp = kladd;
            kladd = null;
            return temp;
        }
    }
}
