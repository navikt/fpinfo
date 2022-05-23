package no.nav.foreldrepenger.info;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity(name = "Aksjonspunkt")
@Table(name = "AKSJONSPUNKT")
@Immutable
public class Aksjonspunkt {

    private static final String OPPRETTET_AKSJONSPUNKT_STATUS_KODE = "OPPR";

    @Id
    @Column(name = "AKSJONSPUNKT_ID")
    private Long id;

    @Column(name = "AKSJONSPUNKT_STATUS")
    private String status;

    @Column(name = "behandling_id")
    private Long behandlingId;

    Aksjonspunkt() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBehandlingId(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public boolean erÅpentAksjonspunkt() {
        return OPPRETTET_AKSJONSPUNKT_STATUS_KODE.equals(this.getStatus());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [id=" + id + ", status=" + status + ", behandlingId=" + behandlingId + "]";
    }
}
