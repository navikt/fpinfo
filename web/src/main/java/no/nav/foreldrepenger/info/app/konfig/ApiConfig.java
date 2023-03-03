package no.nav.foreldrepenger.info.app.konfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import no.nav.foreldrepenger.info.server.TimingFilter;
import no.nav.foreldrepenger.info.v2.SakRest;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.security.token.support.jaxrs.JwtTokenContainerRequestFilter;
import no.nav.vedtak.exception.TekniskException;

@ApplicationPath(ApiConfig.API_URI)
public class ApiConfig extends Application {

    private static final Environment ENV = Environment.current();
    public static final String API_URI = "/api";

    public ApiConfig() {
        var oas = new OpenAPI();
        var info = new Info()
                .title("Vedtaksløsningen - Info")
                .version("1.0")
                .description("REST grensesnitt for fp-info.");

        oas.info(info)
                .addServersItem(new Server()
                        .url(ENV.getProperty("context.path", "/fpinfo")));

        var oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .resourceClasses(getClasses().stream().map(Class::getName).collect(Collectors.toSet()));

        try {
            new GenericOpenApiContextBuilder<>()
                    .openApiConfiguration(oasConfig)
                    .buildContext(true)
                    .read();
        } catch (OpenApiConfigurationException e) {
            throw new TekniskException("OPEN-API", e.getMessage(), e);
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                TimingFilter.class,
                JwtTokenContainerRequestFilter.class,
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
