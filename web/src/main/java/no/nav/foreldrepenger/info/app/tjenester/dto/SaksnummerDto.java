package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.SAKSNUMMER;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

public record SaksnummerDto(@Valid @NotNull @Pattern(regexp = "\\d+") @Size(max = 18) String saksnummer) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(SAKSNUMMER, saksnummer);
    }
}
