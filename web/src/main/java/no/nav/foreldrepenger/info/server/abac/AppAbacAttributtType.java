package no.nav.foreldrepenger.info.server.abac;


import no.nav.vedtak.sikkerhet.abac.AbacAttributtType;

public enum AppAbacAttributtType implements AbacAttributtType {

    ANNEN_PART;

    @Override
    public boolean getMaskerOutput() {
        return false;
    }
}
