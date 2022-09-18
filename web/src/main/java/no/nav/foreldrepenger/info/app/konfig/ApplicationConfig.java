package no.nav.foreldrepenger.info.app.konfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ServerProperties;

import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import no.nav.foreldrepenger.info.app.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.info.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.info.app.exceptions.JsonProcessingExceptionMapper;
import no.nav.foreldrepenger.info.app.jackson.JacksonJsonConfig;
import no.nav.foreldrepenger.info.app.tjenester.DokumentforsendelseTjeneste;
import no.nav.foreldrepenger.info.server.TimingFilter;
import no.nav.foreldrepenger.info.v2.SakRest;
import no.nav.security.token.support.jaxrs.JwtTokenContainerRequestFilter;

@ApplicationPath(ApplicationConfig.API_URI)
public class ApplicationConfig extends Application {

    public static final String API_URI = "/api";

    public ApplicationConfig() throws OpenApiConfigurationException {
        new GenericOpenApiContextBuilder<>()
                .openApiConfiguration(new SwaggerConfiguration()
                        .openAPI(new OpenAPI()
                                .info(new Info()
                                        .title("Vedtaksløsningen - FPInfo")
                                        .version("1.0")
                                        .description("REST grensesnitt for FPInfo."))
                                .addServersItem(new Server()
                                        .url("/api")))
                        .prettyPrint(true)
                        .scannerClass("io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner")
                        .resourcePackages(Set.of("no.nav")))
                .buildContext(true)
                .read();
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                TimingFilter.class,
                JwtTokenContainerRequestFilter.class,
                DokumentforsendelseTjeneste.class,
                SakRest.class,
                ConstraintViolationMapper.class,
                JsonProcessingExceptionMapper.class,
                JacksonJsonConfig.class,
                GeneralRestExceptionMapper.class,
                SwaggerApiResource.class);
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
