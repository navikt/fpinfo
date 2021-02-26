package no.nav.foreldrepenger.info.web.app.startupinfo;

import no.nav.vedtak.exception.TekniskException;

class OppstartFeil {

    private OppstartFeil() {

    }

    static TekniskException uventetExceptionVedOppstart(Exception e) {
        return new TekniskException("FP-753407", "Uventet exception ved oppstart", e);
    }
}
