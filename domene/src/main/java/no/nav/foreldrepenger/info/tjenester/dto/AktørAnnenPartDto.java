package no.nav.foreldrepenger.info.tjenester.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.info.abac.AppAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class AktørAnnenPartDto implements AbacDto {
    @NotNull
    @Digits(integer = 19, fraction = 0)
    private String annenPartAktørId;

    public AktørAnnenPartDto() {
    }

    public AktørAnnenPartDto(String annenPartAktørId) {
        this.annenPartAktørId = annenPartAktørId;
    }

    public String getAnnenPartAktørId() {
        return annenPartAktørId;
    }

    public void setAnnenPartAktørId(String annenPartAktørId) {
        this.annenPartAktørId = annenPartAktørId;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.ANNEN_PART, annenPartAktørId);
    }
}
