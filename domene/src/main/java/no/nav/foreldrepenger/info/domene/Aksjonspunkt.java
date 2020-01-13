package no.nav.foreldrepenger.info.domene;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;


@Entity(name = "Aksjonspunkt")
@Table(name = "AKSJONSPUNKT")
@Immutable
public class Aksjonspunkt extends BaseEntitet {

    public static final String OPPRETTET_AKSJONSPUNKT_STATUS_KODE = "OPPR";

    @Id
    @Column(name = "AKSJONSPUNKT_ID")
    private Long id;

    @Column(name = "AKSJONSPUNKT_STATUS")
    private String status;

    @Column(name = "behandling_id")
    private Long behandlingId;

    public Aksjonspunkt() {
        // Hibernate
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

    public static class Builder {
        private Long id;
        private String status;
        private Long behandlingId;

        public Builder medId(Long id) {
            this.id = id;
            return this;
        }

        public Builder medStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder medBehandling(Long behandlingId) {
            this.behandlingId = behandlingId;
            return this;
        }

        public Aksjonspunkt build() {
            Aksjonspunkt aksjonspunkt = new Aksjonspunkt();
            aksjonspunkt.id = this.id;
            aksjonspunkt.status = this.status;
            aksjonspunkt.behandlingId = this.behandlingId;
            return aksjonspunkt;
        }
    }

    public boolean erÅpentAksjonspunkt() {
        return OPPRETTET_AKSJONSPUNKT_STATUS_KODE.equals(this.getStatus());
    }
}
