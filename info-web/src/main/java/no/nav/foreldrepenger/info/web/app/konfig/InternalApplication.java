package no.nav.foreldrepenger.info.web.app.konfig;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import no.nav.foreldrepenger.info.web.app.metrics.PrometheusRestService;
import no.nav.foreldrepenger.info.web.app.tjenester.HealthChecks;

@ApplicationPath(InternalApplication.API_URL)
public class InternalApplication extends Application {

    static final String API_URL = "/internal";

    public InternalApplication() throws OpenApiConfigurationException {
        new GenericOpenApiContextBuilder<>()
                .openApiConfiguration(new SwaggerConfiguration()
                        .openAPI(new OpenAPI()
                                .info(new Info()
                                        .title("Vedtaksløsningen - FPInfo")
                                        .version("1.0")
                                        .description("REST grensesnitt for FPInfo."))
                                .addServersItem(new Server()
                                        .url(API_URL)))
                        .prettyPrint(true)
                        .scannerClass("io.swagger.v3.jaxrs2.integration.JaxrsAnnotationScanner")
                        .resourcePackages(Set.of("no.nav")))
                .buildContext(true)
                .read();
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(PrometheusRestService.class, HealthChecks.class);
    }

}
