package no.nav.foreldrepenger.info.abac;

import no.nav.vedtak.sikkerhet.abac.AbacAttributtType;

public enum UtvidetAbacAttributtType implements AbacAttributtType {

    ANNEN_PART,
    OPPGITT_ALENEOMSORG;

    private final String sporingsloggEksternKode;
    private final boolean maskerOutput;
    private final boolean valider;

    UtvidetAbacAttributtType() {
        this.sporingsloggEksternKode = null;
        this.maskerOutput = false;
        this.valider = false;
    }

    UtvidetAbacAttributtType(String sporingsloggEksternKode) {
        this.sporingsloggEksternKode = sporingsloggEksternKode;
        this.maskerOutput = false;
        this.valider = false;
    }

    UtvidetAbacAttributtType(String sporingsloggEksternKode, boolean maskerOutput) {
        this.sporingsloggEksternKode = sporingsloggEksternKode;
        this.maskerOutput = maskerOutput;
        this.valider = false;
    }

    UtvidetAbacAttributtType(String sporingsloggEksternKode, boolean maskerOutput, boolean valider) {
        this.sporingsloggEksternKode = sporingsloggEksternKode;
        this.maskerOutput = maskerOutput;
        this.valider = valider;
    }

    public String getSporingsloggEksternKode() {
        return this.sporingsloggEksternKode;
    }

    public boolean getMaskerOutput() {
        return this.maskerOutput;
    }

    public boolean getValider() {
        return this.valider;
    }

}
