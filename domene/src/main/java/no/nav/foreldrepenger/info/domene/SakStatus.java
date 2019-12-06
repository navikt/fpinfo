package no.nav.foreldrepenger.info.domene;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.felles.datatyper.BehandlingTema;
import no.nav.vedtak.felles.jpa.BaseEntitet;


@Entity(name = "SakStatus")
@Table(name = "SAK_STATUS")
@Immutable
public class SakStatus extends BaseEntitet {

    @Id
    @Column(name = "RANDOM_ID")
    private Long randomId;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    @Column(name = "FAGSAK_STATUS")
    private String fagsakStatus;

    @Column(name = "BEHANDLING_ID")
    private String behandlingId;

    @Column(name = "YTELSE_TYPE")
    private String fagsakYtelseType;

    @Column(name = "HOVED_SOEKER_AKTOER_ID")
    private String aktørId;

    @Column(name = "ANPA_AKTØRID")
    private String aktørIdAnnenPart;

    @Column(name = "BARN_AKTOER_ID")
    private String aktørIdBarn;

    @Column(name = "familie_hendelse_type")
    private String familieHendelseType;

    public SakStatus() {
        // Hibernate
    }

    public String getBehandlingstema() {
        return BehandlingTema.fraYtelse(fagsakYtelseType, familieHendelseType);
    }

    public String getAktørIdBarn() {
        return aktørIdBarn;
    }

    public String getBehandlingId() {
        return behandlingId;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getAktørIdAnnenPart() {
        return aktørIdAnnenPart;
    }

    public String getFagsakStatus() {
        return fagsakStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SakStatus sakStatus;

        public Builder() {
            sakStatus = new SakStatus();
        }

        public Builder medSaksnummer(String saksnummer) {
            sakStatus.saksnummer = saksnummer;
            return this;
        }

        public Builder medBehandlingId(String behandlingId) {
            sakStatus.behandlingId = behandlingId;
            return this;
        }

        public Builder medFagsakStatus(String fagsakstatus) {
            sakStatus.fagsakStatus = fagsakstatus;
            return this;
        }

        public Builder medFagsakYtelseType(String fagsakYtelseType) {
            sakStatus.fagsakYtelseType = fagsakYtelseType;
            return this;
        }

        public Builder medAktørId(String aktørId) {
            sakStatus.aktørId = aktørId;
            return this;
        }

        public Builder medAktørIdAnnenPart(String aktørIdAnnenPart) {
            sakStatus.aktørIdAnnenPart = aktørIdAnnenPart;
            return this;
        }

        public Builder medAktørIdBarn(String aktørId) {
            sakStatus.aktørIdBarn = aktørId;
            return this;
        }

        public Builder medFamilieHendelseType(String familiehendelseType) {
            sakStatus.familieHendelseType = familiehendelseType;
            return this;
        }

        public SakStatus build() {
            return sakStatus;
        }
    }
}
