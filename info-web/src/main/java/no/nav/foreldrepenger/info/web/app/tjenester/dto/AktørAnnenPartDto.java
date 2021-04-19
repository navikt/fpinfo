package no.nav.foreldrepenger.info.web.app.tjenester.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.info.web.abac.AppAbacAttributtType;
import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;

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
