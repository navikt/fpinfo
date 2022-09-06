package no.nav.foreldrepenger.info.app.tjenester.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.info.abac.AppAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;


public record AktørAnnenPartDto(@NotNull @Digits(integer = 19, fraction = 0) String annenPartAktørId) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.ANNEN_PART, annenPartAktørId());
    }
}
