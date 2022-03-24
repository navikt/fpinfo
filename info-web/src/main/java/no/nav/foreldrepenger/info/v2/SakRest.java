package no.nav.foreldrepenger.info.v2;

import static no.nav.foreldrepenger.info.abac.AppAbacAttributtType.ANNEN_PART;
import static no.nav.foreldrepenger.info.abac.BeskyttetRessursAttributt.FAGSAK;
import static no.nav.foreldrepenger.info.abac.BeskyttetRessursAttributt.UTTAKSPLAN;
import static no.nav.foreldrepenger.info.server.JettyServer.ACR_LEVEL4;
import static no.nav.foreldrepenger.info.server.JettyServer.TOKENX;
import static no.nav.foreldrepenger.sikkerhet.abac.domene.ActionType.READ;
import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.AKTØR_ID;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;
import no.nav.foreldrepenger.common.innsyn.v2.VedtakPeriode;
import no.nav.foreldrepenger.sikkerhet.abac.AbacDto;
import no.nav.foreldrepenger.sikkerhet.abac.BeskyttetRessurs;
import no.nav.foreldrepenger.sikkerhet.abac.domene.AbacDataAttributter;
import no.nav.security.token.support.core.api.ProtectedWithClaims;

@Path(SakRest.PATH)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@ProtectedWithClaims(issuer = TOKENX, claimMap = { ACR_LEVEL4 })
public class SakRest {

    final static String PATH = "/v2/saker";

    private static final Logger LOG = LoggerFactory.getLogger(SakRest.class);

    private final SakerTjeneste sakerTjeneste;
    private final AnnenPartsVedtaksperioderTjeneste annenPartsVedtaksperioder;

    @Inject
    public SakRest(SakerTjeneste sakerTjeneste, AnnenPartsVedtaksperioderTjeneste annenPartsVedtaksperioder) {
        this.sakerTjeneste = sakerTjeneste;
        this.annenPartsVedtaksperioder = annenPartsVedtaksperioder;
    }

    SakRest() {
        this(null, null);
    }

    @GET
    @BeskyttetRessurs(action = READ, resource = FAGSAK, path = PATH)
    public Saker hentSaker(@NotNull @QueryParam("aktorId") @Parameter(name = "aktorId") AktørIdDto aktørId) {
        LOG.info("Henter saker for bruker");
        var fpSaker = sakerTjeneste.hentFor(map(aktørId.aktørId()));
        var fpSakerDto = fpSaker.stream().map(FpSak::tilDto).collect(Collectors.toSet());
        return new Saker(fpSakerDto, Set.of(), Set.of());
    }

    @Path("/annenpart")
    @GET
    @BeskyttetRessurs(action = READ, resource = UTTAKSPLAN, path = PATH + "/annenPart")
    public List<VedtakPeriode> hentAnnenPartsVedtaksperioder(@NotNull @QueryParam("sokersAktorId") @Parameter(name = "sokersAktorId") AktørIdDto søkersAktørId,
                                                             @NotNull @QueryParam("annenPartAktorId") @Parameter(name = "annenPartAktorId") AktørAnnenPartDto annenPartAktørId,
                                                             @NotNull @QueryParam("barnAktorId") @Parameter(name = "barnAktorId") AktørIdBarnDto barnAktorId) {
        LOG.info("Henter annen parts vedtaksperioder");
        var perioder = annenPartsVedtaksperioder.hentFor(map(søkersAktørId.aktørId()),
                map(annenPartAktørId.aktørId), map(barnAktorId.aktørId()));
        LOG.info("Returnerer annen parts vedtaksperioder. Antall perioder {}", perioder.size());
        return perioder.stream().map(no.nav.foreldrepenger.info.v2.VedtakPeriode::tilDto).toList();
    }

    private AktørId map(String aktørId) {
        return new AktørId(aktørId);
    }

    public record AktørAnnenPartDto(@NotNull @Digits(integer = 19, fraction = 0) String aktørId) implements AbacDto {

        @Override
        public AbacDataAttributter abacAttributter() {
            return AbacDataAttributter.opprett().leggTil(ANNEN_PART, aktørId());
        }
    }

    public record AktørIdDto(@NotNull @Digits(integer = 19, fraction = 0) String aktørId) implements AbacDto {

        @Override
        public AbacDataAttributter abacAttributter() {
            return AbacDataAttributter.opprett().leggTil(AKTØR_ID, aktørId);
        }
    }

    public record AktørIdBarnDto(@NotNull @Digits(integer = 19, fraction = 0) String aktørId) implements AbacDto {

        @Override
        public AbacDataAttributter abacAttributter() {
            return AbacDataAttributter.opprett();
        }
    }
}
