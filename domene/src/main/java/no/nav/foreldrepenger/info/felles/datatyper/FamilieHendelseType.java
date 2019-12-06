package no.nav.foreldrepenger.info.felles.datatyper;

public enum FamilieHendelseType {

    ADOPSJON("ADPSJN"),
    OMSORGOVERDRAGELSE("OMSRGO"),
    FØDSEL("FODSL"),
    TERMIN("TERM");

    private String verdi;

    FamilieHendelseType(String verdi) {
        this.verdi = verdi;
    }

    public String getVerdi() {
        return verdi;
    }

}
