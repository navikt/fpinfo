package no.nav.foreldrepenger.info.web.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.SAKSNUMMER;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

public class SaksnummerDto implements AbacDto {

    @NotNull
    @Pattern(regexp = "\\d+")
    @Size(max = 18)
    private final String saksnummer;

    public SaksnummerDto(@Valid String saksnummer) {
        this.saksnummer = saksnummer;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(SAKSNUMMER, saksnummer);
    }
}
