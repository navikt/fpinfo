package no.nav.foreldrepenger.info.web.app.tjenester.dto;

import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.BEHANDLING_ID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

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
        return AbacDataAttributter.opprett().leggTil(BEHANDLING_ID, getBehandlingId());
    }

    public Long getBehandlingId() {
        return Long.parseLong(behandlingId);
    }
}
