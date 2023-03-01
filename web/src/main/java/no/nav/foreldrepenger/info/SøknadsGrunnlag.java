package no.nav.foreldrepenger.info;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "SøknadsGrunnlag")
@Table(name = "SOEKNAD_GR")
@Immutable
public class SøknadsGrunnlag {

    public static final String MOR_UFØR = "UFØRE";

    @Id
    @Column(name = "RANDOM_ID")
    private Long randomId;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    @Column(name = "BEHANDLING_ID", updatable = false, insertable = false)
    private Long behandlingId;

    @Column(name = "BRUKER_ROLLE")
    private String brukerRolle;

    @Column(name = "DEKNINGSGRAD")
    private Integer dekningsgrad;

    @OneToOne
    @JoinColumn(name = "GRYF_ID")
    private SøknadsGrunnlagRettigheter foreldreRettigheter;

    @OneToOne
    @JoinColumn(name = "BEHANDLING_ID")
    private UføreGrunnlag uføreGrunnlag;

    @Column(name = "MORS_AKTIVITET_UFOERE")
    private String morsAktivitetHvisUfør;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "OENSKER_JUSTERT_UTTAK_VED_FOEDSEL")
    private Boolean ønskerJustertUttakVedFødsel;

    public Long getBehandlingId() {
        return behandlingId;
    }

    public String getBrukerRolle() {
        return brukerRolle;
    }

    public Integer getDekningsgrad() {
        return dekningsgrad;
    }

    public Boolean getAleneomsorg() {
        return foreldreRettigheter.getAleneomsorg();
    }

    public Boolean getAleneomsorgSøknad() {
        return foreldreRettigheter.getSøknadAleneomsorg();
    }

    public Boolean bekreftetMorUfør() {
        var overstyrtUføre = foreldreRettigheter.getSaksbehandlerUføretrygd();
        if (overstyrtUføre != null) {
            return overstyrtUføre;
        }
        //left outer join her, så får et objekt der alle feltene er null
        var registerUføre = uføreGrunnlag.getRegister();
        if (registerUføre != null) {
            return registerUføre;
        }
        return søknadMorUfør();
    }

    public Boolean søknadMorUfør() {
        var morUførSøknad = foreldreRettigheter.getSøknadUføretrygd();
        return morUførSøknad == null ? MOR_UFØR.equals(morsAktivitetHvisUfør) : morUførSøknad;
    }

    public Boolean søknadHarAnnenForelderTilsvarendeRettEØS() {
        return foreldreRettigheter.getSøknadHarAnnenForelderTilsvarendeRettEØS();
    }

    public Boolean bekreftetHarAnnenForelderTilsvarendeRettEØS() {
        var avklart = foreldreRettigheter.getSaksbehandlerHarAnnenForelderTilsvarendeRettEØS();
        if (avklart == null) {
            return søknadHarAnnenForelderTilsvarendeRettEØS();
        }
        return avklart;
    }

    public Boolean ønskerJustertUttakVedFødsel() {
        return ønskerJustertUttakVedFødsel;
    }

    public Boolean getAnnenForelderRett() {
        return foreldreRettigheter.getAnnenForelderRett();
    }

    public Boolean getAnnenForelderRettSøknad() {
        return foreldreRettigheter.getSøknadAnnenForelderRett();
    }

    public static class Builder {

        private SøknadsGrunnlag kladd = new SøknadsGrunnlag();

        public Builder behandlingId(Long behandlingId) {
            kladd.behandlingId = behandlingId;
            return this;
        }

        public Builder brukerRolle(String brukerRolle) {
            kladd.brukerRolle = brukerRolle;
            return this;
        }

        public Builder dekningsgrad(Integer dekningsgrad) {
            kladd.dekningsgrad = dekningsgrad;
            return this;
        }

        public Builder foreldreRettigheter(SøknadsGrunnlagRettigheter foreldreRettigheter) {
            kladd.foreldreRettigheter = foreldreRettigheter;
            return this;
        }

        public Builder uføreGrunnlag(UføreGrunnlag uføreGrunnlag) {
            kladd.uføreGrunnlag = uføreGrunnlag;
            return this;
        }

        public Builder morsAktivitetHvisUfør(String morsAktivitetHvisUfør) {
            kladd.morsAktivitetHvisUfør = morsAktivitetHvisUfør;
            return this;
        }

        public SøknadsGrunnlag build() {
            var tmp = kladd;
            kladd = null;
            return tmp;
        }
    }

    @Override
    public String toString() {
        return "SøknadsGrunnlag{" + "saksnummer='" + saksnummer + '\'' + ", behandlingId=" + behandlingId + ", brukerRolle='" + brukerRolle + '\''
            + ", dekningsgrad=" + dekningsgrad + ", foreldreRettigheter=" + foreldreRettigheter + ", uføreGrunnlag=" + uføreGrunnlag
            + ", morsAktivitetHvisUfør='" + morsAktivitetHvisUfør + '\'' + ", ønskerJustertUttakVedFødsel=" + ønskerJustertUttakVedFødsel + '}';
    }
}
