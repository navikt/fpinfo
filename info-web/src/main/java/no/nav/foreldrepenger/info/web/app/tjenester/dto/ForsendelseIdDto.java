package no.nav.foreldrepenger.info.web.app.tjenester.dto;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import no.nav.foreldrepenger.info.web.abac.AppAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class ForsendelseIdDto implements AbacDto {

    public static final String UUID_REGEXP = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    @NotNull
    @Pattern(regexp = UUID_REGEXP)
    @Size(max = 36)
    private final String forsendelseId;

    public ForsendelseIdDto(@Valid String forsendelseId) {
        this.forsendelseId = forsendelseId;
    }

    public static ForsendelseIdDto fromString(String uuid) {
        return new ForsendelseIdDto(uuid);
    }

    public UUID getForsendelseId() {
        return UUID.fromString(this.forsendelseId);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{forsendelseId='" + this.forsendelseId + '\'' + '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.FORSENDELSE_UUID,
                UUID.fromString(forsendelseId));
    }

}
