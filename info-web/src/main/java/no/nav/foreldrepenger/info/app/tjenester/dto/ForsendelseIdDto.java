package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.foreldrepenger.info.abac.AppAbacAttributtType.FORSENDELSE_UUID;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

public record ForsendelseIdDto(@NotNull UUID forsendelseId) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(FORSENDELSE_UUID, forsendelseId);
    }

}
