package no.nav.foreldrepenger.info.domene;

import java.time.LocalDate;

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

    @Id
    @Column(name = "RANDOM_ID")
    private Long randomId;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "ANTALL_BARN")
    private Integer antallBarn;

    @Column(name = "FAMILIE_HENDELSE_TYPE")
    private String familieHendelseType;

    @Column(name = "OMSORGSOVERTAKELSE_DATO")
    private LocalDate omsorgsovertakelseDato;

    @Column(name = "TERMINDATO")
    private LocalDate termindato;

    @Column(name = "FOEDSEL_DATO")
    private LocalDate fødselDato;

    @Column(name = "BRUKER_ROLLE")
    private String brukerRolle;

    @Column(name = "DEKNINGSGRAD")
    private Integer dekningsgrad;

    @OneToOne
    @JoinColumn(name = "GRYF_ID")
    private SøknadsGrunnlagRettigheter foreldreRettigheter;

    @Column(name = "MORS_AKTIVITET_UFOERE")
    private String morsAktivitetHvisUfør;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "ANNENFORELDERERINFORMERT")
    private Boolean annenForelderInformert;

    public Long getBehandlingId() {
        return behandlingId;
    }

    public Integer getAntallBarn() {
        return antallBarn;
    }

    public String getFamilieHendelseType() {
        return familieHendelseType;
    }

    public LocalDate getOmsorgsovertakelseDato() {
        return omsorgsovertakelseDato;
    }

    public LocalDate getTermindato() {
        return termindato;
    }

    public LocalDate getFødselDato() {
        return fødselDato;
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

    public Boolean getAnnenForelderRett() {
        return foreldreRettigheter.getAnnenForelderRett();
    }

    public String getMorsAktivitetHvisUfør() {
        return morsAktivitetHvisUfør;
    }

    public Boolean getAnnenForelderInformert() {
        return annenForelderInformert;
    }
}
