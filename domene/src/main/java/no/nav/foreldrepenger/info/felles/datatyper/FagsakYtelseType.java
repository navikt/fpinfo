package no.nav.foreldrepenger.info.felles.datatyper;

public enum FagsakYtelseType {
    ENDRING_FP("ENDRING_FP"),
    FP("FP"),
    ES("ES");

    private String verdi;

    FagsakYtelseType(String verdi) {
        this.verdi = verdi;
    }

    public String getVerdi() {
        return verdi;
    }
}
