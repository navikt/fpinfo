package no.nav.foreldrepenger.info.felles.datatyper;

public enum FagsakYtelseType {
    ENDRING_FP,
    FP,
    ES;

    public String getVerdi() {
        return name();
    }
}