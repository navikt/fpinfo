package no.nav.foreldrepenger.info.felles.datatyper;

public enum BehandlingResultatType {
    IKKE_FASTSATT,
    INNVILGET,
    AVSLÅTT,
    OPPHØR,
    HENLAGT_SØKNAD_TRUKKET,
    HENLAGT_FEILOPPRETTET,
    HENLAGT_BRUKER_DØD,
    MERGET_OG_HENLAGT,
    HENLAGT_SØKNAD_MANGLER,
    FORELDREPENGER_ENDRET,
    INGEN_ENDRING,
    KLAGE_AVVIST,
    KLAGE_MEDHOLD,
    KLAGE_YTELSESVEDTAK_OPPHEVET,
    KLAGE_YTELSESVEDTAK_STADFESTET,
    HENLAGT_KLAGE_TRUKKET,
    INNSYN_INNVILGET,
    INNSYN_DELVIS_INNVILGET,
    INNSYN_AVVIST;

    public String getVerdi() {
        return name();
    }
}