package no.nav.foreldrepenger.info.abac;

import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacAttributtType;

public enum AppAbacAttributtType implements AbacAttributtType {

    FORSENDELSE_UUID,
    ANNEN_PART,
    OPPGITT_ALENEOMSORG;

    @Override
    public boolean getMaskerOutput() {
        return false;
    }
}
