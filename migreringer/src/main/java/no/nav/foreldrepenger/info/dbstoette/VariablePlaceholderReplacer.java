package no.nav.foreldrepenger.info.dbstoette;

import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.util.env.Environment;

public class VariablePlaceholderReplacer {

    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";
    private static final Environment ENV = Environment.current();
    private static final Logger LOG = LoggerFactory.getLogger(VariablePlaceholderReplacer.class);
    private final Properties properties;

    public VariablePlaceholderReplacer() {
        this(new Properties());
    }

    @Deprecated
    public VariablePlaceholderReplacer(Properties properties) {
        this.properties = properties;
        if (!properties.isEmpty()) {
            LOG.warn(
                    "Ikke konstruer denne klassen med properties, bruk heller en av application.properties-variantene");
        }
    }

    public String replacePlaceholders(String input) {
        var key = input.replace(PLACEHOLDER_PREFIX, "").replace(PLACEHOLDER_SUFFIX, "");
        return key != input ? Optional.ofNullable(ENV.getProperty(key))
                .orElse(properties.getProperty(key)) : input;
    }
}
