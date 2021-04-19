package no.nav.foreldrepenger.info.web.abac;

import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacAttributtType;

public enum AppAbacAttributtType implements AbacAttributtType {

    FORSENDELSE_UUID,
    ANNEN_PART,
    OPPGITT_ALENEOMSORG;

    public static final String RESOURCE_FORELDREPENGER_ALENEOMSORG = "no.nav.abac.attributter.resource.foreldrepenger.aleneomsorg";
    public static final String RESOURCE_FORELDREPENGER_ANNEN_PART = "no.nav.abac.attributter.resource.foreldrepenger.annen_part";

    private final boolean maskerOutput;

    AppAbacAttributtType() {
        this(false);
    }

    AppAbacAttributtType(boolean maskerOutput) {
        this.maskerOutput = maskerOutput;
    }

    @Override
    public boolean getMaskerOutput() {
        return maskerOutput;
    }
}
