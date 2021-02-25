package no.nav.foreldrepenger.info.web.app.exceptions;

public record FeltFeilDto(String navn, String melding, String metainformasjon) {

    public FeltFeilDto(String navn, String melding) {
        this(navn, melding, null);
    }

}
