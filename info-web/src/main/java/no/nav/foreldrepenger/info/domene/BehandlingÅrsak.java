package no.nav.foreldrepenger.info.domene;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.felles.datatyper.BehandlingÅrsakType;

@Entity(name = "BehandlingÅrsak")
@Table(name = "BEHANDLING_ARSAK")
@Immutable
public class BehandlingÅrsak {

    @Id
    @Column(name = "BEHANDLING_ARSAK_ID")
    private Long id;

    @Column(name = "BEHANDLING_ARSAK_TYPE")
    @Convert(converter = BehandlingÅrsakType.KodeverdiConverter.class)
    private BehandlingÅrsakType type;

    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    public BehandlingÅrsak() {
        // Hibernate
    }

    public BehandlingÅrsak(BehandlingÅrsakType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public BehandlingÅrsakType getType() {
        return type;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BehandlingÅrsak that = (BehandlingÅrsak) o;
        return type == that.type && Objects.equals(behandlingId, that.behandlingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, behandlingId);
    }

    @Override
    public String toString() {
        return "BehandlingÅrsak{" + "type='" + type + '\'' + ", behandlingId=" + behandlingId + '}';
    }
}
