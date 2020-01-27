package no.nav.foreldrepenger.info.dbstoette;

import no.nav.vedtak.util.env.Environment;

public final class VariablePlaceholderReplacer {

    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";
    private static final Environment ENV = Environment.current();

    public String replacePlaceholders(String placeHolder) {
        return sjekk(ENV.getProperty(placeHolder
                .replace(PLACEHOLDER_PREFIX, "")
                .replace(PLACEHOLDER_SUFFIX, ""),
                placeHolder));
    }

    private static String sjekk(String verdi) {
        if (verdi.contains(PLACEHOLDER_SUFFIX)) {
            throw new IllegalStateException(
                    "Ingen verdi funnet for placeholder " + verdi + ", sjekk konfigurasjonen");
        }
        return verdi;
    }
}
