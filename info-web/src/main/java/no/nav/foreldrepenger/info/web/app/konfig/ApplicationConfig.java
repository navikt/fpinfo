package no.nav.foreldrepenger.info.web.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import no.nav.foreldrepenger.info.web.app.exceptions.ConstraintViolationMapper;
import no.nav.foreldrepenger.info.web.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.info.web.app.exceptions.JsonMappingExceptionMapper;
import no.nav.foreldrepenger.info.web.app.exceptions.JsonParseExceptionMapper;
import no.nav.foreldrepenger.info.web.app.jackson.JacksonJsonConfig;
import no.nav.foreldrepenger.info.web.app.tjenester.DokumentforsendelseTjeneste;
import no.nav.security.token.support.jaxrs.JwtTokenContainerRequestFilter;

@ApplicationPath(ApplicationConfig.API_URI)
public class ApplicationConfig extends Application {

    static final String API_URI = "/api";

    public ApplicationConfig() throws OpenApiConfigurationException {

        new GenericOpenApiContextBuilder<>()
                .openApiConfiguration(new SwaggerConfiguration()
                        .openAPI(new OpenAPI()
                                .info(new Info()
                                        .title("Vedtaksløsningen - Info")
                                        .version("1.0")
                                        .description("REST grensesnitt for Vedtaksløsningen."))
                                .addServersItem(new Server()
                                        .url("/fpinfo" + API_URI)))
                        .prettyPrint(true)
                        .scannerClass("io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner")
                        .resourcePackages(Set.of("no.nav")))
                .buildContext(true)
                .read();

    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                JwtTokenContainerRequestFilter.class,
                DokumentforsendelseTjeneste.class,
                SwaggerSerializers.class,
                OpenApiResource.class,
                ConstraintViolationMapper.class,
                JsonMappingExceptionMapper.class,
                JsonParseExceptionMapper.class,
                JacksonJsonConfig.class,
                GeneralRestExceptionMapper.class);
    }
}
