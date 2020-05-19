package no.nav.foreldrepenger.info.domene;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.felles.datatyper.BehandlingResultatType;
import no.nav.vedtak.util.env.Environment;

@Entity(name = "Behandling")
@Table(name = "BEHANDLING")
@Immutable
public class Behandling extends BaseEntitet {

    private static final Environment ENV = Environment.current();

    @Id
    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "BEHANDLING_STATUS")
    private String behandlingStatus;

    @Column(name = "BEHANDLING_RESULTAT_TYPE")
    private String behandlingResultatType;

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

    @Column(name = "BEHANDLING_ARSAK")
    private String behandlingÅrsak;

    @OneToMany(mappedBy = "behandlingId")
    private List<Aksjonspunkt> aksjonspunkter = new ArrayList<>(1);

    public Behandling() {
        // Hibernate
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

    public String getBehandlingÅrsak() {
        return behandlingÅrsak;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public List<Aksjonspunkt> getAksjonspunkter() {
        return aksjonspunkter;
    }

    public List<Aksjonspunkt> getÅpneAksjonspunkter() {
        return this.aksjonspunkter.stream().filter(Aksjonspunkt::erÅpentAksjonspunkt).collect(Collectors.toList());
    }

    public Boolean erHenlagt() {
        if (!ENV.isProd()) {
            return false;
        }
        return BehandlingResultatType.HENLAGT_BRUKER_DØD.getVerdi().equalsIgnoreCase(behandlingResultatType) ||
                BehandlingResultatType.HENLAGT_FEILOPPRETTET.getVerdi().equalsIgnoreCase(behandlingResultatType) ||
                BehandlingResultatType.HENLAGT_KLAGE_TRUKKET.getVerdi().equalsIgnoreCase(behandlingResultatType) ||
                BehandlingResultatType.HENLAGT_SØKNAD_MANGLER.getVerdi().equalsIgnoreCase(behandlingResultatType) ||
                BehandlingResultatType.HENLAGT_SØKNAD_TRUKKET.getVerdi().equalsIgnoreCase(behandlingResultatType) ||
                BehandlingResultatType.MERGET_OG_HENLAGT.getVerdi().equalsIgnoreCase(behandlingResultatType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Behandling behandling;

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

        public Builder medSaksnummer(String saksnummer) {
            behandling.saksnummer = saksnummer;
            return this;
        }

        public Builder medBehandlingÅrsak(String behandlingÅrsak) {
            behandling.behandlingÅrsak = behandlingÅrsak;
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

        public Builder medAksjonspunkter(List<Aksjonspunkt> aksjonspunkter) {
            behandling.aksjonspunkter = aksjonspunkter;
            return this;
        }

        public Behandling build() {
            return behandling;
        }
    }
}
