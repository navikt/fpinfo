package no.nav.foreldrepenger.info.abac;

import no.nav.vedtak.sikkerhet.abac.AbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public enum AppAbacAttributtType implements AbacAttributtType {

    FORSENDELSE_UUID("forsendelseUuid"),
    ANNEN_PART("annen_part"),
    OPPGITT_ALENEOMSORG("oppgitt_aleneomsorg");

    public static AbacAttributtType AKTØR_ID = StandardAbacAttributtType.AKTØR_ID;

    public static AbacAttributtType BEHANDLING_ID = StandardAbacAttributtType.BEHANDLING_ID;

    public static AbacAttributtType SAKSNUMMER = StandardAbacAttributtType.SAKSNUMMER;

    public static final String RESOURCE_FORELDREPENGER_ALENEOMSORG = "no.nav.abac.attributter.resource.foreldrepenger.aleneomsorg";
    public static final String RESOURCE_FORELDREPENGER_ANNEN_PART = "no.nav.abac.attributter.resource.foreldrepenger.annen_part";

    private final boolean maskerOutput;
    private final String sporingsloggEksternKode;

    AppAbacAttributtType() {
        sporingsloggEksternKode = null;
        maskerOutput = false;
    }

    AppAbacAttributtType(String sporingsloggEksternKode) {
        this.sporingsloggEksternKode = sporingsloggEksternKode;
        this.maskerOutput = false;
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
