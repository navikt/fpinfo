package no.nav.foreldrepenger.info.tjenester.dto;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import no.nav.foreldrepenger.info.abac.AppAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class SaksnummerDto implements AbacDto {

    @NotNull
    @Pattern(regexp = "\\d+")
    @Size(max = 18)
    private final String saksnummer;

    public SaksnummerDto(@Valid String saksnummer) {
        this.saksnummer = saksnummer;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.SAKSNUMMER, saksnummer);
    }

    public String getSaksnummer() {
        return saksnummer;
    }
}

