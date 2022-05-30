package no.nav.foreldrepenger.info;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "UførGrunnlag")
@Table(name = "UFORE_GRUNNLAG")
@Immutable
public class UføreGrunnlag {

    @Id
    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "REGISTER_UFORE")
    private Boolean register;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "OVERSTYRT_UFORE")
    private Boolean overstyrt;

    public UføreGrunnlag(Long behandlingId,
                         Boolean register,
                         Boolean overstyrt) {
        this.behandlingId = behandlingId;
        this.register = register;
        this.overstyrt = overstyrt;
    }

    UføreGrunnlag() {
    }

    public Boolean getRegister() {
        return register;
    }

    public Boolean getOverstyrt() {
        return overstyrt;
    }

    public Boolean getGjeldende() {
        return Optional.ofNullable(getOverstyrt()).orElse(getRegister());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UføreGrunnlag that = (UføreGrunnlag) o;
        return behandlingId.equals(that.behandlingId) && Objects.equals(register, that.register) && Objects.equals(
                overstyrt, that.overstyrt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, register, overstyrt);
    }
}
