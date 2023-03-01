package no.nav.foreldrepenger.info;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.datatyper.BehandlingStatus;
import no.nav.foreldrepenger.info.datatyper.BehandlingType;

@Entity(name = "Behandling")
@Table(name = "BEHANDLING")
@Immutable
public class Behandling {

    @Id
    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "BEHANDLING_STATUS")
    private String behandlingStatus;

    @Column(name = "BEHANDLING_TYPE")
    @Convert(converter = BehandlingType.KodeverdiConverter.class)
    private BehandlingType behandlingType;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    @OneToMany(mappedBy = "behandlingId")
    private List<BehandlingÅrsak> årsaker = new ArrayList<>();

    @Column(name = "opprettet_tid", nullable = false)
    private LocalDateTime opprettetTidspunkt;

    @Column(name = "endret_tid")
    private LocalDateTime endretTidspunkt;

    Behandling() {
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public String getBehandlingStatus() {
        return behandlingStatus;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public List<BehandlingÅrsak> getÅrsaker() {
        return årsaker;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public boolean erAvsluttet() {
        return getBehandlingStatus().equals(BehandlingStatus.AVSLUTTET.getVerdi())
                || getBehandlingStatus().equals(BehandlingStatus.IVERKSETTER_VEDTAK.getVerdi());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Behandling behandling;

        public Builder() {
            this.behandling = new Behandling();
        }

        public Builder medBehandlingId(Long behandlingId) {
            behandling.behandlingId = behandlingId;
            return this;
        }

        public Builder medBehandlingStatus(String behandlingStatus) {
            behandling.behandlingStatus = behandlingStatus;
            return this;
        }

        public Builder medSaksnummer(Saksnummer saksnummer) {
            behandling.saksnummer = saksnummer == null ? null : saksnummer.saksnummer();
            return this;
        }

        public Builder medBehandlingÅrsaker(List<BehandlingÅrsak> årsaker) {
            behandling.årsaker = årsaker;
            return this;
        }

        public Builder medBehandlingType(BehandlingType behandlingType) {
            behandling.behandlingType = behandlingType;
            return this;
        }

        public Behandling build() {
            return behandling;
        }
    }

    @Override
    public String toString() {
        return "Behandling{" + "behandlingId=" + behandlingId + ", behandlingStatus='" + behandlingStatus + '\'' + ", behandlingType="
            + behandlingType + ", saksnummer='" + saksnummer + '\'' + ", årsaker=" + årsaker
            + ", opprettetTidspunkt=" + opprettetTidspunkt + ", endretTidspunkt=" + endretTidspunkt + '}';
    }
}
