package no.nav.foreldrepenger.info.datatyper;

public enum FamilieHendelseType {

    ADOPSJON("ADPSJN"),
    OMSORGOVERDRAGELSE("OMSRGO"),
    FØDSEL("FODSL"),
    TERMIN("TERM");

    private final String verdi;

    FamilieHendelseType(String verdi) {
        this.verdi = verdi;
    }

    public String getVerdi() {
        return verdi;
    }

}
