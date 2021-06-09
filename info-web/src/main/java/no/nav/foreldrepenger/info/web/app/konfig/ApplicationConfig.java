package no.nav.foreldrepenger.info.web.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import no.nav.foreldrepenger.info.web.app.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.info.web.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.info.web.app.exceptions.JsonProcessingExceptionMapper;
import no.nav.foreldrepenger.info.web.app.jackson.JacksonJsonConfig;
import no.nav.foreldrepenger.info.web.app.tjenester.DokumentforsendelseTjeneste;
import no.nav.foreldrepenger.info.web.server.TimingFilter;
import no.nav.security.token.support.jaxrs.JwtTokenContainerRequestFilter;

@ApplicationPath(ApplicationConfig.API_URI)
public class ApplicationConfig extends Application {

    static final String API_URI = "/api";

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                TimingFilter.class,
                JwtTokenContainerRequestFilter.class,
                DokumentforsendelseTjeneste.class,
                SwaggerSerializers.class,
                OpenApiResource.class,
                ConstraintViolationMapper.class,
                JsonProcessingExceptionMapper.class,
                JacksonJsonConfig.class,
                GeneralRestExceptionMapper.class);
    }

}
