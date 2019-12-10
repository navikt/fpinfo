package no.nav.foreldrepenger.info.tjenester.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public class AktørIdDto implements AbacDto {

    @NotNull
    @Digits(integer = 19, fraction = 0)
    private String aktørId;

    public AktørIdDto() {
    }

    public AktørIdDto(String aktørId) {
        this.aktørId = aktørId;
    }

    public String getAktørId() {
        return aktørId;
    }

    public void setAktørId(String aktørId) {
        this.aktørId = aktørId;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.AKTØR_ID, aktørId);
    }
}
