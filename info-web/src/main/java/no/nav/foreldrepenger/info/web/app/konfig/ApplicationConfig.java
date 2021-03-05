package no.nav.foreldrepenger.info.web.app.konfig;

import java.util.Collections;
import java.util.HashSet;
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
import no.nav.foreldrepenger.info.web.app.exceptions.JsonMappingExceptionMapper;
import no.nav.foreldrepenger.info.web.app.exceptions.JsonParseExceptionMapper;
import no.nav.foreldrepenger.info.web.app.tjenester.DokumentforsendelseTjeneste;

@ApplicationPath(ApplicationConfig.API_URI)
public class ApplicationConfig extends Application {

    public static final String API_URI = "/api";

    public ApplicationConfig() {

        OpenAPI oas = new OpenAPI();
        Info info = new Info()
                .title("Vedtaksløsningen - Info")
                .version("1.0")
                .description("REST grensesnitt for Vedtaksløsningen.");

        oas.info(info)
                .addServersItem(new Server()
                        .url("/fpinfo" + API_URI));
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .scannerClass("io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner")
                .resourcePackages(Set.of("no.nav"));
        try {
            new GenericOpenApiContextBuilder<>()
                    .openApiConfiguration(oasConfig)
                    .buildContext(true)
                    .read();
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // classes.add(JwtTokenContainerRequestFilter.class);
        classes.add(DokumentforsendelseTjeneste.class);
        classes.add(SwaggerSerializers.class);
        classes.add(OpenApiResource.class);
        classes.add(ConstraintViolationMapper.class);
        classes.add(JsonMappingExceptionMapper.class);
        classes.add(JsonParseExceptionMapper.class);
        classes.addAll(FellesKlasserForRest.getClasses());
        return Collections.unmodifiableSet(classes);
    }
}
