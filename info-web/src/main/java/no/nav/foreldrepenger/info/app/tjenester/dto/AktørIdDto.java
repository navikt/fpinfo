package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.AKTØR_ID;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

public record AktørIdDto(@NotNull @Digits(integer = 19, fraction = 0) String aktørId) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(AKTØR_ID, aktørId);
    }
}
