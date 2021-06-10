package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.BEHANDLING_ID;

import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;
import no.nav.vedtak.exception.TekniskException;

public record SøknadXmlDto(String xml, String journalpostId, Long behandlingId) implements AbacDto {

    public SøknadXmlDto(MottattDokument dokument) {
        this(dokument.getSøknadXml(), dokument.getJournalpostId(), dokument.getBehandlingId());
    }

    public static SøknadXmlDto fraDomene(MottattDokument dokument1, MottattDokument dokument2) {
        if (!dokument1.getBehandlingId().equals(dokument2.getBehandlingId())) {
            throw new TekniskException("FP-241631", "Dokumentene gjelder ikke samme behandling");
        }
        return new SøknadXmlDto(dokument1.getSøknadXml() != null ? dokument1.getSøknadXml() : dokument2.getSøknadXml(),
                dokument1.getJournalpostId() != null ? dokument1.getJournalpostId()
                        : dokument2.getJournalpostId(),
                dokument1.getBehandlingId() != null ? dokument1.getBehandlingId()
                        : dokument2.getBehandlingId());

    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(BEHANDLING_ID, behandlingId);
    }

}
