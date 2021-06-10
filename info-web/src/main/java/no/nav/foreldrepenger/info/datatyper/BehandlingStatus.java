package no.nav.foreldrepenger.info.datatyper;

public enum BehandlingStatus {

    AVSLUTTET("AVSLU"),
    FATTER_VEDTAK("FVED"),
    IVERKSETTER_VEDTAK("IVED"),
    OPPRETTET("OPPRE"),
    UTREDES("UTRED");

    private final String verdi;

    BehandlingStatus(String verdi) {
        this.verdi = verdi;
    }

    public String getVerdi() {
        return verdi;
    }

}
