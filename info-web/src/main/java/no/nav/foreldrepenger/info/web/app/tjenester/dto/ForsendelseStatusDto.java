package no.nav.foreldrepenger.info.web.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.SAKSNUMMER;

import com.fasterxml.jackson.annotation.JsonInclude;

import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForsendelseStatusDto implements AbacDto {
    private ForsendelseStatus forsendelseStatus;

    /**
     * Joark journalpostid.
     */
    private Long journalpostId;

    /**
     * GSAK Saksnummer. (samme som Fagsak#saksnummer).
     */
    private Long saksnummer;

    public ForsendelseStatusDto(ForsendelseStatus forsendelseStatus) {
        this.forsendelseStatus = forsendelseStatus;
    }

    public ForsendelseStatus getForsendelseStatus() {
        return forsendelseStatus;
    }

    public void setForsendelseStatus(ForsendelseStatus forsendelseStatus) {
        this.forsendelseStatus = forsendelseStatus;
    }

    public Long getJournalpostId() {
        return journalpostId;
    }

    public void setJournalpostId(Long journalpostId) {
        this.journalpostId = journalpostId;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public void setSaksnummer(Long saksnummer) {
        this.saksnummer = saksnummer;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(SAKSNUMMER, String.valueOf(saksnummer));
    }
}
