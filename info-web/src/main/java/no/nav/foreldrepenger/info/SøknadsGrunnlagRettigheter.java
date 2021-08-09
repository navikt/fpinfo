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
    @Column(name = "SAKSB_ANNEN_FORELDER_HAR_RETT")
    private Boolean saksbehandlerAnnenForelderRett;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SO_ANNEN_FORELDER_RETT")
    private Boolean søknadAnnenForelderRett;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SAKSB_ALENEOMSORG")
    private Boolean saksbehandlerAleneomsorg;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "SO_ALENEOMSORG")
    private Boolean søknadAleneomsorg;

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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [gryfId=" + gryfId + ", saksbehandlerAnnenForelderRett=" + saksbehandlerAnnenForelderRett
                + ", søknadAnnenForelderRett=" + søknadAnnenForelderRett + ", saksbehandlerAleneomsorg=" + saksbehandlerAleneomsorg
                + ", søknadAleneomsorg=" + søknadAleneomsorg + "]";
    }
}
