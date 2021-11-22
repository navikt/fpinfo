package no.nav.foreldrepenger.info;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.datatyper.BehandlingTema;

@Entity(name = "SakStatus")
@Table(name = "SAK_STATUS")
@Immutable
public class Sak {

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

    @Column(name = "fagsak_opprettet_tid", nullable = false)
    private LocalDateTime opprettetTidspunkt;

    @Column(name = "endret_tid")
    private LocalDateTime endretTidspunkt;

    Sak() {
    }

    public String getBehandlingstema() {
        return BehandlingTema.fraYtelse(fagsakYtelseType, familieHendelseType);
    }

    public String getFagsakYtelseType() {
        return fagsakYtelseType;
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

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [randomId=" + randomId + ", saksnummer=" + saksnummer + ", fagsakStatus=" + fagsakStatus
                + ", behandlingId="
                + behandlingId + ", fagsakYtelseType=" + fagsakYtelseType + ", aktørId=" + aktørId + ", aktørIdAnnenPart="
                + aktørIdAnnenPart + ", aktørIdBarn=" + aktørIdBarn + ", familieHendelseType=" + familieHendelseType
                + ", opprettetTidspunkt=" + opprettetTidspunkt + ", endretTidspunkt=" + endretTidspunkt + "]";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Sak sak;

        public Builder() {
            sak = new Sak();
        }

        public Builder medSaksnummer(Saksnummer saksnummer) {
            sak.saksnummer = saksnummer.saksnummer();
            return this;
        }

        public Builder medBehandlingId(String behandlingId) {
            sak.behandlingId = behandlingId;
            return this;
        }

        public Builder medFagsakStatus(String fagsakstatus) {
            sak.fagsakStatus = fagsakstatus;
            return this;
        }

        public Builder medFagsakYtelseType(String fagsakYtelseType) {
            sak.fagsakYtelseType = fagsakYtelseType;
            return this;
        }

        public Builder medAktørId(String aktørId) {
            sak.aktørId = aktørId;
            return this;
        }

        public Builder medAktørIdAnnenPart(String aktørIdAnnenPart) {
            sak.aktørIdAnnenPart = aktørIdAnnenPart;
            return this;
        }

        public Builder medAktørIdBarn(String aktørId) {
            sak.aktørIdBarn = aktørId;
            return this;
        }

        public Builder medFamilieHendelseType(String familiehendelseType) {
            sak.familieHendelseType = familiehendelseType;
            return this;
        }

        public Sak build() {
            return sak;
        }
    }
}
