package no.nav.foreldrepenger.info.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.BEHANDLING_ID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

public record BehandlingIdDto(@NotNull @Pattern(regexp = "\\d+") @Size(max = 18) String behandlingId) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(BEHANDLING_ID, getBehandlingId());
    }

    public Long getBehandlingId() {
        return Long.parseLong(behandlingId());
    }
}
