package no.nav.foreldrepenger.info;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "SøknadsGrunnlagRettigheter")
@Table(name = "SOEKNADS_GR_RETTIGHETER")
@Immutable
public class SøknadsGrunnlagRettigheter {


    @Id
    @Column(name = "GRYF_ID")
    private Long gryfId;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SB_ANNEN_FORELDER_RETT")
    private Boolean saksbehandlerAnnenForelderRett;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SO_ANNEN_FORELDER_RETT")
    private Boolean søknadAnnenForelderRett;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SB_ALENEOMSORG")
    private Boolean saksbehandlerAleneomsorg;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SO_ALENEOMSORG")
    private Boolean søknadAleneomsorg;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SB_UFORETRYGD")
    private Boolean saksbehandlerUføretrygd;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SO_UFORETRYGD")
    private Boolean søknadUføretrygd;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SO_ANNEN_FORELDER_RETT_EOS")
    private Boolean søknadHarAnnenForelderTilsvarendeRettEØS;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SB_ANNEN_FORELDER_RETT_EOS")
    private Boolean saksbehandlerHarAnnenForelderTilsvarendeRettEØS;

    public SøknadsGrunnlagRettigheter(Long gryfId,
                                      Boolean saksbehandlerAnnenForelderRett,
                                      Boolean søknadAnnenForelderRett,
                                      Boolean saksbehandlerAleneomsorg,
                                      Boolean søknadAleneomsorg,
                                      Boolean saksbehandlerUføretrygd, Boolean søknadUføretrygd,
                                      Boolean søknadHarAnnenForelderTilsvarendeRettEØS,
                                      Boolean saksbehandlerHarAnnenForelderTilsvarendeRettEØS) {
        this.gryfId = gryfId;
        this.saksbehandlerAnnenForelderRett = saksbehandlerAnnenForelderRett;
        this.søknadAnnenForelderRett = søknadAnnenForelderRett;
        this.saksbehandlerAleneomsorg = saksbehandlerAleneomsorg;
        this.søknadAleneomsorg = søknadAleneomsorg;
        this.saksbehandlerUføretrygd = saksbehandlerUføretrygd;
        this.søknadUføretrygd = søknadUføretrygd;
        this.søknadHarAnnenForelderTilsvarendeRettEØS = søknadHarAnnenForelderTilsvarendeRettEØS;
        this.saksbehandlerHarAnnenForelderTilsvarendeRettEØS = saksbehandlerHarAnnenForelderTilsvarendeRettEØS;
    }

    protected SøknadsGrunnlagRettigheter() {

    }

    public Boolean getAnnenForelderRett() {
        return saksbehandlerAnnenForelderRett != null
                ? saksbehandlerAnnenForelderRett
                : søknadAnnenForelderRett;
    }

    public Boolean getAleneomsorg() {
        return saksbehandlerAleneomsorg != null
                ? saksbehandlerAleneomsorg
                : søknadAleneomsorg;
    }

    public Boolean getSøknadAnnenForelderRett() {
        return søknadAnnenForelderRett;
    }

    public Boolean getSøknadAleneomsorg() {
        return søknadAleneomsorg;
    }

    public Boolean getSaksbehandlerUføretrygd() {
        return saksbehandlerUføretrygd;
    }

    public Boolean getSøknadUføretrygd() {
        return søknadUføretrygd;
    }

    public Boolean getSøknadHarAnnenForelderTilsvarendeRettEØS() {
        return søknadHarAnnenForelderTilsvarendeRettEØS;
    }

    public Boolean getSaksbehandlerHarAnnenForelderTilsvarendeRettEØS() {
        return saksbehandlerHarAnnenForelderTilsvarendeRettEØS;
    }

    @Override
    public String toString() {
        return "SøknadsGrunnlagRettigheter{" + "saksbehandlerAnnenForelderRett=" + saksbehandlerAnnenForelderRett
                + ", søknadAnnenForelderRett=" + søknadAnnenForelderRett + ", saksbehandlerAleneomsorg="
                + saksbehandlerAleneomsorg + ", søknadAleneomsorg=" + søknadAleneomsorg + ", saksbehandlerUføretrygd="
                + saksbehandlerUføretrygd + ", søknadUføretrygd=" + søknadUføretrygd + ", søknadHarAnnenForelderTilsvarendeRettEØS="
                + søknadHarAnnenForelderTilsvarendeRettEØS + ", saksbehandlerHarAnnenForelderTilsvarendeRettEØS="
                + saksbehandlerHarAnnenForelderTilsvarendeRettEØS + '}';
    }
}
