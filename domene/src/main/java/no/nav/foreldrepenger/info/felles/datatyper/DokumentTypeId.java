package no.nav.foreldrepenger.info.felles.datatyper;

public enum DokumentTypeId {
    SØKNAD_FORELDREPENGER_FØDSEL("SØKNAD_FORELDREPENGER_FØDSEL"),
    SØKNAD_FORELDREPENGER_ADOPSJON("SØKNAD_FORELDREPENGER_ADOPSJON"),
    SØKNAD_ENGANGSSTØNAD_FØDSEL("SØKNAD_ENGANGSSTØNAD_FØDSEL"),
    SØKNAD_ENGANGSSTØNAD_ADOPSJON("SØKNAD_ENGANGSSTØNAD_ADOPSJON"),
    FORELDREPENGER_ENDRING_SØKNAD("FORELDREPENGER_ENDRING_SØKNAD"),
    INNTEKTSMELDING("INNTEKTSMELDING");

    private final String verdi;

    DokumentTypeId(String verdi) {
        this.verdi = verdi;
    }

    public String getVerdi() {
        return verdi;
    }
}
