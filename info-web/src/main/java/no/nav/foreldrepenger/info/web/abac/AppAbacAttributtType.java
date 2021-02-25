package no.nav.foreldrepenger.info.web.abac;

import no.nav.vedtak.sikkerhet.abac.AbacAttributtType;

public enum AppAbacAttributtType implements AbacAttributtType {

    FORSENDELSE_UUID("forsendelseUuid"),
    ANNEN_PART("annen_part"),
    OPPGITT_ALENEOMSORG("oppgitt_aleneomsorg");

    public static final String RESOURCE_FORELDREPENGER_ALENEOMSORG = "no.nav.abac.attributter.resource.foreldrepenger.aleneomsorg";
    public static final String RESOURCE_FORELDREPENGER_ANNEN_PART = "no.nav.abac.attributter.resource.foreldrepenger.annen_part";

    private final boolean maskerOutput;
    private final String sporingsloggEksternKode;

    AppAbacAttributtType(String sporingsloggEksternKode) {
        this(sporingsloggEksternKode, false);
    }

    AppAbacAttributtType(String sporingsloggEksternKode, boolean maskerOutput) {
        this.sporingsloggEksternKode = sporingsloggEksternKode;
        this.maskerOutput = maskerOutput;
    }

    @Override
    public boolean getMaskerOutput() {
        return maskerOutput;
    }

    @Override
    public String getSporingsloggKode() {
        return sporingsloggEksternKode;
    }

}
