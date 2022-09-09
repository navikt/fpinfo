package no.nav.foreldrepenger.info.app.tjenester.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public record SaksnummerDto(@Valid @NotNull @Pattern(regexp = "\\d+") @Size(max = 18) String saksnummer) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.SAKSNUMMER, saksnummer);
    }
}
