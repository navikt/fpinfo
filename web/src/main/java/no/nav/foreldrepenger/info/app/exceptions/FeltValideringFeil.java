package no.nav.foreldrepenger.info.app.exceptions;

import static java.lang.String.format;

import java.util.List;

import no.nav.vedtak.exception.FunksjonellException;

class FeltValideringFeil {

    private FeltValideringFeil() {

    }

    static FunksjonellException feltverdiKanIkkeValideres(List<String> feltnavn) {
        return new FunksjonellException("FP-328673",
                format("Det oppstod en valideringsfeil på felt %s. Vennligst kontroller at alle feltverdier er korrekte.", feltnavn),
                "Kontroller at alle feltverdier er korrekte");
    }
}
