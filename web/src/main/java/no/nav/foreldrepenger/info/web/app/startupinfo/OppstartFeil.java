package no.nav.foreldrepenger.info.web.app.startupinfo;

import no.nav.vedtak.exception.TekniskException;

class OppstartFeil {

    static TekniskException uventetExceptionVedOppstart(Exception e) {
        return new TekniskException("FP-753407", "Uventet exception ved oppstart");

    }

    static TekniskException selftestStatus(String status, String description, String endpoint) {
        return new TekniskException("FP-753409", String.format("Selftest %s: %s. Endpoint: %s", status, description));

    }
}
