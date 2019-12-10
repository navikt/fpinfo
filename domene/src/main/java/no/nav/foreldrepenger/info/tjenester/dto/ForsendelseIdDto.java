package no.nav.foreldrepenger.info.tjenester.dto;

import java.util.UUID;

import javax.validation.Valid;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public class ForsendelseIdDto implements AbacDto {

    private final UUID forsendelseId;

    public ForsendelseIdDto(@Valid String forsendelseId) {
        this.forsendelseId = UUID.fromString(forsendelseId);
    }

    public static ForsendelseIdDto fromString(String uuid) {
        return new ForsendelseIdDto(uuid);
    }

    public UUID getForsendelseId() {
        return forsendelseId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{forsendelseId='" + this.forsendelseId + '\'' + '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_UUID, forsendelseId);
    }

}