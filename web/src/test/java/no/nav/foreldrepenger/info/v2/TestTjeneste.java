package no.nav.foreldrepenger.info.v2;

import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;
import static no.nav.foreldrepenger.info.v2.SakRest.map;
import static no.nav.foreldrepenger.info.v2.SakRest.tilFpSakerDto;
import static no.nav.foreldrepenger.info.v2.SakRest.tilSvpSakerDto;

import java.util.Set;

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
import no.nav.foreldrepenger.common.innsyn.v2.Saker;
import no.nav.foreldrepenger.info.app.tjenester.SøknadsGrunnlagTjeneste;
import no.nav.foreldrepenger.info.app.tjenester.dto.SaksnummerDto;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.security.token.support.core.api.Unprotected;

/**
 * For lokalt test. Laget egen tjeneste for å unngå alle security filters/tokenx lokalt
 */
@Path("test")
@Produces(MediaType.APPLICATION_JSON)
@Unprotected
@ApplicationScoped
public class TestTjeneste {

    private static final Environment ENV = Environment.current();

    private SøknadsGrunnlagTjeneste grunnlag;
    private FpSakerTjeneste fpSakerTjeneste;
    private SvpSakerTjeneste svpSakerTjeneste;

    static {
        if (!ENV.isLocal()) {
            throw new IllegalStateException("Skal ikke brukes i noe annet enn test");
        }
    }

    @Inject
    public TestTjeneste(SøknadsGrunnlagTjeneste grunnlag, FpSakerTjeneste fpSakerTjeneste, SvpSakerTjeneste svpSakerTjeneste) {
        this.grunnlag = grunnlag;
        this.fpSakerTjeneste = fpSakerTjeneste;
        this.svpSakerTjeneste = svpSakerTjeneste;
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

    @GET
    @Path("v2")
    public Saker v2(@NotNull @QueryParam("aktørId") @Parameter(name = "aktørId") @Valid SakRest.AktørIdDto aktørIdDto) {
        var fpSaker = fpSakerTjeneste.hentFor(map(aktørIdDto.aktørId()));
        var svpSaker = svpSakerTjeneste.hentFor(map(aktørIdDto.aktørId()));
        var fpSakerDto = tilFpSakerDto(fpSaker);
        var svpSakerDto = tilSvpSakerDto(svpSaker);
        return new Saker(fpSakerDto, Set.of(), svpSakerDto);
    }
}
