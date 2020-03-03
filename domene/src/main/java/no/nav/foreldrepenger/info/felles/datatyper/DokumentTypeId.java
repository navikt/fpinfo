package no.nav.foreldrepenger.info.felles.datatyper;

public enum DokumentTypeId {
    SØKNAD_FORELDREPENGER_FØDSEL("SØKNAD_FORELDREPENGER_FØDSEL"), //$NON-NLS-1$
    SØKNAD_FORELDREPENGER_ADOPSJON("SØKNAD_FORELDREPENGER_ADOPSJON"), //$NON-NLS-1$
    SØKNAD_ENGANGSSTØNAD_FØDSEL("SØKNAD_ENGANGSSTØNAD_FØDSEL"), //$NON-NLS-1$
    SØKNAD_ENGANGSSTØNAD_ADOPSJON("SØKNAD_ENGANGSSTØNAD_ADOPSJON"), //$NON-NLS-1$
    FORELDREPENGER_ENDRING_SØKNAD("FORELDREPENGER_ENDRING_SØKNAD"), //$NON-NLS-1$
    INNTEKTSMELDING("INNTEKTSMELDING"); //$NON-NLS-1$

    private String verdi;

    DokumentTypeId(String verdi) {
        this.verdi = verdi;
    }

    public String getVerdi() {
        return verdi;
    }
}
