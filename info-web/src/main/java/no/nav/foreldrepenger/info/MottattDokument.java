package no.nav.foreldrepenger.info;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import no.nav.foreldrepenger.info.datatyper.DokumentTypeId;

@Immutable
@Entity(name = "MottattDokument")
@Table(name = "MOTTATT_DOKUMENT")
public class MottattDokument implements Serializable {

    @Id
    @Column(name = "MOTTATT_DOKUMENT_ID")
    private String mottattDokumentId;

    @Id
    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "SOEKNAD_XML")
    private String søknadXml;

    @Column(name = "JOURNALPOST_ID")
    private String journalpostId;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    MottattDokument(MottattDokument mottattDokument) {
        this.behandlingId = mottattDokument.behandlingId;
        this.mottattDokumentId = mottattDokument.mottattDokumentId;
        this.søknadXml = mottattDokument.søknadXml;
        this.journalpostId = mottattDokument.journalpostId;
        this.type = mottattDokument.type;
        this.saksnummer = mottattDokument.saksnummer;
    }

    MottattDokument() {
    }

    public Long getBehandlingId() {
        return behandlingId;
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
        return DokumentTypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON.name().equalsIgnoreCase(type) ||
                DokumentTypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL.name().equalsIgnoreCase(type) ||
                DokumentTypeId.SØKNAD_FORELDREPENGER_ADOPSJON.name().equalsIgnoreCase(type) ||
                DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL.name().equalsIgnoreCase(type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MottattDokument mottattDokumentMal;

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

        public Builder medBehandlingId(Long behandlingId) {
            mottattDokumentMal.behandlingId = behandlingId;
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

        public Builder medType(DokumentTypeId dokumentTypeId) {
            mottattDokumentMal.type = dokumentTypeId.name();
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

    @Override
    public String toString() {
        return "MottattDokument{" + "mottattDokumentId='" + mottattDokumentId + '\'' + ", behandlingId=" + behandlingId
                + '\'' + ", journalpostId='" + journalpostId + '\'' + ", type='" + type
                + '\'' + ", saksnummer='" + saksnummer + '\'' + '}';
    }
}
