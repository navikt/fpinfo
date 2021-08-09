package no.nav.foreldrepenger.info.app.exceptions;

import java.util.Collection;
import java.util.List;

record FeilDto(String feilmelding, Collection<FeltFeilDto> feltFeil, FeilType type) {

    FeilDto(String msg, Collection<FeltFeilDto> feil) {
        this(msg, feil, null);
    }

    FeilDto(String msg, FeilType feilType) {
        this(msg, List.of(), feilType);
    }

    FeilDto(FeilType feilType, String msg) {
        this(msg, List.of(), feilType);
    }

    FeilDto(String msg) {
        this(msg, List.of(), null);
    }

}
