package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.SAKSNUMMER;

import com.fasterxml.jackson.annotation.JsonInclude;

import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

@JsonInclude(JsonInclude.Include.NON_NULL)

public record ForsendelseStatusDto(ForsendelseStatus forsendelseStatus, Long journalpostId, Long saksnummer) implements AbacDto {

    public ForsendelseStatusDto(ForsendelseStatus forsendelseStatus) {
        this(forsendelseStatus, null, null);
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(SAKSNUMMER, String.valueOf(saksnummer()));
    }
}
