package no.nav.foreldrepenger.info.tjenester.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForsendelseStatusDataDTO implements AbacDto {
    private ForsendelseStatus forsendelseStatus;

    /**
     * Joark journalpostid.
     */
    private Long journalpostId;// TODO HUMLE: endre til String

    /**
     * GSAK Saksnummer. (samme som Fagsak#saksnummer).
     */
    private Long saksnummer;// TODO HUMLE: endre til String

    public ForsendelseStatusDataDTO(ForsendelseStatus forsendelseStatus) {
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
        return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.SAKSNUMMER, String.valueOf(saksnummer));
    }
}