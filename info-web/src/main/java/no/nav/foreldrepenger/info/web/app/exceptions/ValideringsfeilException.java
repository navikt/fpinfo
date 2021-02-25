package no.nav.foreldrepenger.info.web.app.exceptions;

import java.util.Collection;

class ValideringsfeilException extends RuntimeException {
    private final Collection<FeltFeilDto> feltFeil;

    ValideringsfeilException(Collection<FeltFeilDto> feltFeil) {
        this.feltFeil = feltFeil;
    }

    Collection<FeltFeilDto> getFeltFeil() {
        return feltFeil;
    }

}
