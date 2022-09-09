package no.nav.foreldrepenger.info.app.tjenester.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public record AktørIdDto(@NotNull @Digits(integer = 19, fraction = 0) String aktørId) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.AKTØR_ID, aktørId);
    }
}
