package no.nav.foreldrepenger.info.web.app.tjenester.dto;

import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.web.abac.AppAbacAttributtType;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class SøknadXmlDto implements AbacDto {

    private String xml;
    private String journalpostId;
    private Long behandlingId;

    private SøknadXmlDto() {
        // -\**/-
    }

    public String getXml() {
        return xml;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SøknadXmlDto fraDomene(MottattDokument dokument) {
        SøknadXmlDto dto = new SøknadXmlDto();
        dto.xml = dokument.getSøknadXml();
        dto.journalpostId = dokument.getJournalpostId();
        dto.behandlingId = dokument.getBehandlingId();
        return dto;
    }

    public static SøknadXmlDto fraDomene(MottattDokument dokument1, MottattDokument dokument2) {
        if (!dokument1.getBehandlingId().equals(dokument2.getBehandlingId())) {
            throw new TekniskException("FP-241631", "Dokumentene gjelder ikke samme behandling");
        }
        SøknadXmlDto dto = new SøknadXmlDto();
        dto.xml = dokument1.getSøknadXml() != null ? dokument1.getSøknadXml() : dokument2.getSøknadXml();
        dto.journalpostId = dokument1.getJournalpostId() != null ? dokument1.getJournalpostId()
                : dokument2.getJournalpostId();
        dto.behandlingId = dokument1.getBehandlingId() != null ? dokument1.getBehandlingId()
                : dokument2.getBehandlingId();
        return dto;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.BEHANDLING_ID, behandlingId);
    }

    public static class Builder {
        private SøknadXmlDto søknadXmlDto;

        public Builder() {
            søknadXmlDto = new SøknadXmlDto();
        }

        public Builder medXml(String xml) {
            søknadXmlDto.xml = xml;
            return this;
        }

        public Builder medJournalpostId(String journalpostId) {
            søknadXmlDto.journalpostId = journalpostId;
            return this;
        }

        public SøknadXmlDto build() {
            return this.søknadXmlDto;
        }
    }

}
