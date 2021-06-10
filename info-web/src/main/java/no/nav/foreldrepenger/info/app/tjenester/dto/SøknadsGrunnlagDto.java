package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.SAKSNUMMER;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.abac.AppAbacAttributtType;
import no.nav.foreldrepenger.info.datatyper.FamilieHendelseType;
import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

public class SøknadsGrunnlagDto implements AbacDto {

    private static final Logger LOG = LoggerFactory.getLogger(SøknadsGrunnlagDto.class);

    private static final String MOR_ROLLE = "MORA";
    private static final String MOR_UFØR = "UFØRE";

    private String saksnummer;

    @JsonIgnore
    private String annenPartFraSak;

    private String familieHendelseType;
    private LocalDate familieHendelseDato;

    private Integer dekningsgrad;
    private Integer antallBarn;

    private Boolean søkerErFarEllerMedmor;
    private Boolean morErAleneOmOmsorg;
    private Boolean morHarRett;
    private Boolean morErUfør;

    private Boolean farMedmorErAleneOmOmsorg;
    private Boolean farMedmorHarRett;

    private Boolean annenForelderErInformert;

    private LocalDate fødselsdato;
    private LocalDate termindato;
    private LocalDate omsorgsovertakelsesdato;

    private List<UttaksPeriodeDto> uttaksPerioder;

    public static SøknadsGrunnlagDto fraDomene(Saksnummer saksnummer, SøknadsGrunnlag grunnlag) {
        SøknadsGrunnlagDto dto = new SøknadsGrunnlagDto();
        dto.saksnummer = saksnummer.saksnummer();
        dto.familieHendelseType = grunnlag.getFamilieHendelseType();
        dto.termindato = grunnlag.getTermindato();

        if (FamilieHendelseType.FØDSEL.getVerdi().equals(grunnlag.getFamilieHendelseType())) {
            dto.familieHendelseDato = grunnlag.getFødselDato();
            dto.fødselsdato = grunnlag.getFødselDato();
        } else if (FamilieHendelseType.TERMIN.getVerdi().equals(grunnlag.getFamilieHendelseType())) {
            dto.familieHendelseDato = grunnlag.getTermindato();
        } else if (FamilieHendelseType.ADOPSJON.getVerdi().equals(grunnlag.getFamilieHendelseType())
                || FamilieHendelseType.OMSORGOVERDRAGELSE.getVerdi().equals(grunnlag.getFamilieHendelseType())) {
            dto.familieHendelseDato = grunnlag.getOmsorgsovertakelseDato();
            dto.omsorgsovertakelsesdato = grunnlag.getOmsorgsovertakelseDato();
        }

        dto.antallBarn = grunnlag.getAntallBarn();
        dto.dekningsgrad = grunnlag.getDekningsgrad();

        boolean søkerErMor = MOR_ROLLE.equals(grunnlag.getBrukerRolle());

        dto.søkerErFarEllerMedmor = !søkerErMor;
        dto.morErAleneOmOmsorg = søkerErMor && grunnlag.getAleneomsorg();
        dto.farMedmorErAleneOmOmsorg = !søkerErMor && grunnlag.getAleneomsorg();

        dto.farMedmorHarRett = !søkerErMor || grunnlag.getAnnenForelderRett();
        dto.morHarRett = søkerErMor || grunnlag.getAnnenForelderRett();

        dto.morErUfør = MOR_UFØR.equals(grunnlag.getMorsAktivitetHvisUfør());

        dto.annenForelderErInformert = grunnlag.getAnnenForelderInformert();

        return dto;
    }

    public SøknadsGrunnlagDto medUttaksPerioder(List<UttaksPeriodeDto> uttaksPerioder) {
        this.uttaksPerioder = uttaksPerioder;
        return this;
    }

    public void setAnnenPartFraSak(String annenPartFraSak) {
        this.annenPartFraSak = annenPartFraSak;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getFamilieHendelseType() {
        return familieHendelseType;
    }

    public LocalDate getFamilieHendelseDato() {
        return familieHendelseDato;
    }

    public Integer getDekningsgrad() {
        return dekningsgrad;
    }

    public Integer getAntallBarn() {
        return antallBarn;
    }

    public Boolean getSøkerErFarEllerMedmor() {
        return søkerErFarEllerMedmor;
    }

    public Boolean getMorErAleneOmOmsorg() {
        return morErAleneOmOmsorg;
    }

    public Boolean getMorHarRett() {
        return morHarRett;
    }

    public Boolean getMorErUfør() {
        return morErUfør;
    }

    public Boolean getFarMedmorErAleneOmOmsorg() {
        return farMedmorErAleneOmOmsorg;
    }

    public Boolean getFarMedmorHarRett() {
        return farMedmorHarRett;
    }

    public Boolean getAnnenForelderErInformert() {
        return annenForelderErInformert;
    }

    public List<UttaksPeriodeDto> getUttaksPerioder() {
        return uttaksPerioder;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public LocalDate getTermindato() {
        return termindato;
    }

    public LocalDate getOmsorgsovertakelsesdato() {
        return omsorgsovertakelsesdato;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        LOG.info("abac dto kalles {}", saksnummer);

        AbacDataAttributter abacDataAttributter = AbacDataAttributter.opprett()
                .leggTil(SAKSNUMMER, saksnummer)
                .leggTil(AppAbacAttributtType.OPPGITT_ALENEOMSORG, morErAleneOmOmsorg);
        if (annenPartFraSak != null) {
            abacDataAttributter.leggTil(AppAbacAttributtType.ANNEN_PART, annenPartFraSak);
        }
        return abacDataAttributter;
    }
}
