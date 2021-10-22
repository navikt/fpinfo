package no.nav.foreldrepenger.info;

import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.info.app.tjenester.SøknadsGrunnlagTjeneste;
import no.nav.foreldrepenger.info.app.tjenester.dto.SaksnummerDto;
import no.nav.foreldrepenger.konfig.Environment;

/**
 * For lokalt test. Laget egen tjeneste for å unngå alle security filters/tokenx lokalt
 */
@Path("test")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TestTjeneste {

    private static final Environment ENV = Environment.current();

    private SøknadsGrunnlagTjeneste grunnlag;

    static {
        if (!ENV.isLocal()) {
            throw new IllegalStateException("Skal ikke brukes i noe annet enn test");
        }
    }

    @Inject
    public TestTjeneste(SøknadsGrunnlagTjeneste grunnlag) {
        this.grunnlag = grunnlag;

    }

    TestTjeneste() {
        //CDI
    }

    @GET
    public Response hentSøknadsgrunnlag(@NotNull @QueryParam("saksnummer") @Parameter(name = "saksnummer") @Valid SaksnummerDto saksnummer) {
        return grunnlag.hentSøknadsgrunnlag(saksnummer, false)
                .map(d -> ok(d).build())
                .orElse(noContent().build());
    }
}
