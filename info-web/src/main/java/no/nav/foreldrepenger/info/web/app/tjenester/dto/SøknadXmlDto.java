package no.nav.foreldrepenger.info.web.app.tjenester.dto;

import static no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType.BEHANDLING_ID;

import no.nav.foreldrepenger.info.domene.MottattDokument;
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

    public static SøknadXmlDto fraDomene(MottattDokument dokument) {
        var dto = new SøknadXmlDto();
        dto.xml = dokument.getSøknadXml();
        dto.journalpostId = dokument.getJournalpostId();
        dto.behandlingId = dokument.getBehandlingId();
        return dto;
    }

    public static SøknadXmlDto fraDomene(MottattDokument dokument1, MottattDokument dokument2) {
        if (!dokument1.getBehandlingId().equals(dokument2.getBehandlingId())) {
            throw new TekniskException("FP-241631", "Dokumentene gjelder ikke samme behandling");
        }
        var dto = new SøknadXmlDto();
        dto.xml = dokument1.getSøknadXml() != null ? dokument1.getSøknadXml() : dokument2.getSøknadXml();
        dto.journalpostId = dokument1.getJournalpostId() != null ? dokument1.getJournalpostId()
                : dokument2.getJournalpostId();
        dto.behandlingId = dokument1.getBehandlingId() != null ? dokument1.getBehandlingId()
                : dokument2.getBehandlingId();
        return dto;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(BEHANDLING_ID, behandlingId);
    }

}
