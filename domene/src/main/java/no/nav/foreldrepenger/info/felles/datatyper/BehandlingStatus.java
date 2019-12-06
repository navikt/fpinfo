package no.nav.foreldrepenger.info.felles.datatyper;


public enum BehandlingStatus {

    AVSLUTTET("AVSLU"), //$NON-NLS-1$
    FATTER_VEDTAK("FVED"), //$NON-NLS-1$
    IVERKSETTER_VEDTAK("IVED"), //$NON-NLS-1$
    OPPRETTET("OPPRE"), //$NON-NLS-1$
    UTREDES("UTRED"); //$NON-NLS-1$

    private String verdi;

    BehandlingStatus(String verdi) {
        this.verdi = verdi;
    }

    public String getVerdi() {
        return verdi;
    }

}
