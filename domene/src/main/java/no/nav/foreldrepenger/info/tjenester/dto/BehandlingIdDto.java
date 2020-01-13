package no.nav.foreldrepenger.info.tjenester.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import no.nav.foreldrepenger.info.abac.AppAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class BehandlingIdDto implements AbacDto {

    @NotNull
    @Pattern(regexp = "\\d+")
    @Size(max = 18)
    private final String behandlingId;

    public BehandlingIdDto(@Valid String behandlingId) {
        this.behandlingId = behandlingId;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.BEHANDLING_ID, behandlingId);
    }

    public Long getBehandlingId() {
        return Long.parseLong(behandlingId);
    }
}
