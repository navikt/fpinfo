package no.nav.foreldrepenger.info.app.exceptions;

import java.util.List;

import no.nav.vedtak.exception.FunksjonellException;

class FeltValideringFeil {

    private FeltValideringFeil() {

    }

    static FunksjonellException feltverdiKanIkkeValideres(List<String> feltnavn) {
        return new FunksjonellException("FP-328673",
                String.format("Det oppstod en valideringsfeil på felt %s. Vennligst kontroller at alle feltverdier er korrekte.", feltnavn),
                "Kontroller at alle feltverdier er korrekte");
    }
}
