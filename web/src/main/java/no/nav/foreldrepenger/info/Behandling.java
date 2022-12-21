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

    @Column(name = "BEHANDLING_RESULTAT_TYPE")
    private String behandlingResultatType;

    @Column(name = "BEHANDLING_TYPE")
    @Convert(converter = BehandlingType.KodeverdiConverter.class)
    private BehandlingType behandlingType;

    @Column(name = "FAGSAK_YTELSE_TYPE")
    private String fagsakYtelseType;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    @Column(name = "FAMILIE_HENDELSE_TYPE")
    private String familieHendelseType;

    @Column(name = "BEHANDLENDE_ENHET")
    private String behandlendeEnhet;

    @Column(name = "BEHANDLENDE_ENHET_NAVN")
    private String behandlendeEnhetNavn;

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

    public String getBehandlingResultatType() {
        return behandlingResultatType;
    }

    public String getFagsakYtelseType() {
        return fagsakYtelseType;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getFamilieHendelseType() {
        return familieHendelseType;
    }

    public List<BehandlingÅrsak> getÅrsaker() {
        return årsaker;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
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

        public Builder medBehandlingResultatType(String behandlingResultatType) {
            behandling.behandlingResultatType = behandlingResultatType;
            return this;
        }

        public Builder medFagsakYtelseType(String fagsakYtelseType) {
            behandling.fagsakYtelseType = fagsakYtelseType;
            return this;
        }

        public Builder medSaksnummer(Saksnummer saksnummer) {
            behandling.saksnummer = saksnummer == null ? null : saksnummer.saksnummer();
            return this;
        }

        public Builder medFamilieHendelseType(String familieHendelseType) {
            behandling.familieHendelseType = familieHendelseType;
            return this;
        }

        public Builder medBehandlendeEnhet(String enhetKode, String enhetNavn) {
            behandling.behandlendeEnhet = enhetKode;
            behandling.behandlendeEnhetNavn = enhetNavn;
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
        return getClass().getSimpleName() + " [behandlingId=" + behandlingId + ", behandlingStatus=" + behandlingStatus
                + ", behandlingResultatType="
                + behandlingResultatType + ", behandlingType=" + behandlingType + ", fagsakYtelseType=" + fagsakYtelseType + ", saksnummer="
                + saksnummer + ", familieHendelseType=" + familieHendelseType + ", behandlendeEnhet=" + behandlendeEnhet
                + ", behandlendeEnhetNavn=" + behandlendeEnhetNavn + ", årsaker=" + årsaker
                + ", opprettetTidspunkt=" + opprettetTidspunkt + ", endretTidspunkt=" + endretTidspunkt + "]";
    }
}
