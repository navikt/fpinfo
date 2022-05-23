package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.foreldrepenger.info.abac.AppAbacAttributtType.ANNEN_PART;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

public record AktørAnnenPartDto(@NotNull @Digits(integer = 19, fraction = 0) String annenPartAktørId) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(ANNEN_PART, annenPartAktørId());
    }
}
