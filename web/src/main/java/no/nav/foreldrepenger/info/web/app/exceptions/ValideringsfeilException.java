package no.nav.foreldrepenger.info.web.app.exceptions;

import java.util.Collection;

public class ValideringsfeilException extends RuntimeException {
    private final Collection<FeltFeilDto> feltFeil;

    public ValideringsfeilException(Collection<FeltFeilDto> feltFeil) {
        this.feltFeil = feltFeil;
    }

    public Collection<FeltFeilDto> getFeltFeil() {
        return feltFeil;
    }

}
