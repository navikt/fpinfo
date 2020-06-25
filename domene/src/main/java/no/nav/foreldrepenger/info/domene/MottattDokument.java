package no.nav.foreldrepenger.info.domene;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.felles.datatyper.DokumentTypeId;

@Immutable
@Entity(name = "MottattDokument")
@Table(name = "MOTTATT_DOKUMENT")
public class MottattDokument extends BaseEntitet {

    @Column(name = "BEHANDLING_STATUS")
    private String behandlingStatus;

    @Id
    @Column(name = "MOTTATT_DOKUMENT_ID")
    private String mottattDokumentId;

    @Id
    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "FORSENDELSE_ID")
    private UUID forsendelseId;

    @Column(name = "SOEKNAD_XML")
    private String søknadXml;

    @Column(name = "JOURNALPOST_ID")
    private String journalpostId;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    MottattDokument(MottattDokument mottattDokument) {
        this.behandlingStatus = mottattDokument.behandlingStatus;
        this.behandlingId = mottattDokument.behandlingId;
        this.forsendelseId = mottattDokument.forsendelseId;
        this.mottattDokumentId = mottattDokument.mottattDokumentId;
        this.søknadXml = mottattDokument.søknadXml;
        this.journalpostId = mottattDokument.journalpostId;
        this.type = mottattDokument.type;
        this.saksnummer = mottattDokument.saksnummer;
    }

    MottattDokument() {
        // Hibernate
    }

    public String getBehandlingStatus() {
        return behandlingStatus;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public UUID getForsendelseId() {
        return forsendelseId;
    }

    public String getMottattDokumentId() {
        return mottattDokumentId;
    }

    public String getSøknadXml() {
        return søknadXml;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public String getType() {
        return type;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public Boolean erSøknad() {
        return DokumentTypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON.getVerdi().equalsIgnoreCase(type) ||
                DokumentTypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL.getVerdi().equalsIgnoreCase(type) ||
                DokumentTypeId.SØKNAD_FORELDREPENGER_ADOPSJON.getVerdi().equalsIgnoreCase(type) ||
                DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL.getVerdi().equalsIgnoreCase(type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MottattDokument mottattDokumentMal;

        public Builder() {
            mottattDokumentMal = new MottattDokument();
        }

        public Builder(MottattDokument mottattDokument) {
            if (mottattDokument != null) {
                mottattDokumentMal = new MottattDokument(mottattDokument);
            } else {
                mottattDokumentMal = new MottattDokument();
            }
        }

        public Builder medBehandlingStatus(String behandlingStatus) {
            mottattDokumentMal.behandlingStatus = behandlingStatus;
            return this;
        }

        public Builder medBehandlingId(Long behandlingId) {
            mottattDokumentMal.behandlingId = behandlingId;
            return this;
        }

        public Builder medForsendelseId(UUID forsendelseId) {
            mottattDokumentMal.forsendelseId = forsendelseId;
            return this;
        }

        public Builder medMottattDokumentId(String mottattDokumentId) {
            mottattDokumentMal.mottattDokumentId = mottattDokumentId;
            return this;
        }

        public Builder medSøknadXml(String søknadXml) {
            mottattDokumentMal.søknadXml = søknadXml;
            return this;
        }

        public Builder medJournalpostId(String journalpostId) {
            mottattDokumentMal.journalpostId = journalpostId;
            return this;
        }

        public Builder medType(String dokumentTypeId) {
            mottattDokumentMal.type = dokumentTypeId;
            return this;
        }

        public Builder medSaksnummer(String saksnummer) {
            mottattDokumentMal.saksnummer = saksnummer;
            return this;
        }

        public MottattDokument build() {
            return mottattDokumentMal;
        }
    }

    @Override
    public boolean equals(Object arg0) {
        return ((arg0 instanceof MottattDokument) &&
                (this.mottattDokumentId != null) && (this.behandlingId != null) &&
                this.mottattDokumentId.equals(((MottattDokument) arg0).getMottattDokumentId()) &&
                this.behandlingId.equals(((MottattDokument) arg0).getBehandlingId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.mottattDokumentId, this.behandlingId);
    }
}
