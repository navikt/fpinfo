package no.nav.foreldrepenger.info.web.app.konfig;

import java.util.Collection;
import java.util.Set;

import no.nav.foreldrepenger.info.web.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.info.web.app.jackson.JacksonJsonConfig;

class FellesKlasserForRest {

    private static final Set<Class<?>> CLASSES = Set.of(JacksonJsonConfig.class, GeneralRestExceptionMapper.class);

    private FellesKlasserForRest() {

    }

    public static Collection<Class<?>> getClasses() {
        return CLASSES;
    }
}
