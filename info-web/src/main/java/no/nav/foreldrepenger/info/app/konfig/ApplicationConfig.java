package no.nav.foreldrepenger.info.app.konfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ServerProperties;

import no.nav.foreldrepenger.info.app.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.info.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.info.app.exceptions.JsonProcessingExceptionMapper;
import no.nav.foreldrepenger.info.app.jackson.JacksonJsonConfig;
import no.nav.foreldrepenger.info.app.tjenester.DokumentforsendelseTjeneste;
import no.nav.foreldrepenger.info.server.TimingFilter;
import no.nav.security.token.support.jaxrs.JwtTokenContainerRequestFilter;
import no.nav.security.token.support.jaxrs.servlet.JaxrsJwtTokenValidationFilter;

@ApplicationPath(ApplicationConfig.API_URI)
public class ApplicationConfig extends Application {

    static final String API_URI = "/api";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                TimingFilter.class,
                JaxrsJwtTokenValidationFilter.class,
                JwtTokenContainerRequestFilter.class,
                DokumentforsendelseTjeneste.class,
                ConstraintViolationMapper.class,
                JsonProcessingExceptionMapper.class, // Vil antagelig tape mot Jacksons JsonParse/JsonMapping - jf exceptiondistanse
                JacksonJsonConfig.class,
                GeneralRestExceptionMapper.class);
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        // Ref Jersey doc
        properties.put(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        properties.put(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, true);
        return properties;
    }

}
