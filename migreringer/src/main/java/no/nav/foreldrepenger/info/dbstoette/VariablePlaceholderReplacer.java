package no.nav.foreldrepenger.info.dbstoette;

import no.nav.vedtak.util.env.Environment;

public class VariablePlaceholderReplacer {

    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";
    private static final Environment ENV = Environment.current();

    public String replacePlaceholders(String input) {
        var key = input.replace(PLACEHOLDER_PREFIX, "").replace(PLACEHOLDER_SUFFIX, "");
        return key != input ? ENV.getRequiredProperty(key) : input;
    }
}
