package no.nav.foreldrepenger.info.app.tjenester.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public record BehandlingIdDto(@NotNull @Pattern(regexp = "\\d+") @Size(max = 18) String behandlingId) implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_ID, getBehandlingId());
    }

    public Long getBehandlingId() {
        return Long.parseLong(behandlingId());
    }
}
