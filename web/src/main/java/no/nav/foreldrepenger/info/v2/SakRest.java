package no.nav.foreldrepenger.info.v2;

import static no.nav.foreldrepenger.info.abac.AppAbacAttributtType.ANNEN_PART;
import static no.nav.foreldrepenger.info.server.JettyServer.ACR_LEVEL4;
import static no.nav.foreldrepenger.info.server.JettyServer.TOKENX;
import static no.nav.vedtak.log.util.LoggerUtils.mask;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.swagger.v3.oas.annotations.Parameter;
import no.nav.foreldrepenger.common.innsyn.AnnenPartVedtak;
import no.nav.foreldrepenger.common.innsyn.Saker;
import no.nav.foreldrepenger.konfig.Environment;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ActionType;
import no.nav.vedtak.sikkerhet.abac.beskyttet.ResourceType;

@Path(SakRest.PATH)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@ProtectedWithClaims(issuer = TOKENX, claimMap = {ACR_LEVEL4})
public class SakRest {

    final static String PATH = "/v2";

    private static final Logger LOG = LoggerFactory.getLogger(SakRest.class);
    private static final Environment ENV = Environment.current();

    private final FpSakerTjeneste fpSakerTjeneste;
    private final SvpSakerTjeneste svpSakerTjeneste;
    private final EsSakerTjeneste esSakerTjeneste;
    private final AnnenPartVedtakTjeneste annenPartVedtakTjeneste;

    @Inject
    public SakRest(FpSakerTjeneste fpSakerTjeneste,
                   SvpSakerTjeneste svpSakerTjeneste,
                   EsSakerTjeneste esSakerTjeneste,
                   AnnenPartVedtakTjeneste annenPartVedtakTjeneste) {
        this.fpSakerTjeneste = fpSakerTjeneste;
        this.svpSakerTjeneste = svpSakerTjeneste;
        this.esSakerTjeneste = esSakerTjeneste;
        this.annenPartVedtakTjeneste = annenPartVedtakTjeneste;
    }

    SakRest() {
        this(null, null, null, null);
    }

    @Path("/saker")
    @GET
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.FAGSAK)
    public Saker hentSaker(@NotNull @QueryParam("aktorId") @Parameter(name = "aktorId") AktørIdDto aktørId) {
        LOG.info("Henter saker for bruker");
        var fpSakerDto = hentFpSaker(aktørId);
        var esSakerDto = hentEsSaker(aktørId);
        var svpSakerDto = hentSvpSaker(aktørId);
        LOG.info("Returnerer {} fp saker, {} es saker, {} svp saker for bruker", fpSakerDto.size(), esSakerDto.size(),
                svpSakerDto.size());
        return new Saker(fpSakerDto, esSakerDto, svpSakerDto);
    }

    private Set<no.nav.foreldrepenger.common.innsyn.EsSak> hentEsSaker(AktørIdDto aktørId) {
        var esSaker = esSakerTjeneste.hentFor(map(aktørId.aktørId));
        return tilEsSakerDto(esSaker);
    }

    private Set<no.nav.foreldrepenger.common.innsyn.SvpSak> hentSvpSaker(AktørIdDto aktørId) {
        var svpSaker = svpSakerTjeneste.hentFor(map(aktørId.aktørId));
        return tilSvpSakerDto(svpSaker);
    }

    private Set<no.nav.foreldrepenger.common.innsyn.FpSak> hentFpSaker(AktørIdDto aktørId) {
        var fpSaker = fpSakerTjeneste.hentFor(map(aktørId.aktørId));
        return tilFpSakerDto(fpSaker);
    }

    public static Set<no.nav.foreldrepenger.common.innsyn.FpSak> tilFpSakerDto(Set<FpSak> fpSaker) {
        return fpSaker.stream().map(FpSak::tilDto).collect(Collectors.toSet());
    }

    public static Set<no.nav.foreldrepenger.common.innsyn.EsSak> tilEsSakerDto(Set<EsSak> esSaker) {
        return esSaker.stream().map(EsSak::tilDto).collect(Collectors.toSet());
    }

    public static Set<no.nav.foreldrepenger.common.innsyn.SvpSak> tilSvpSakerDto(Set<SvpSak> svpSaker) {
        return svpSaker.stream().map(SvpSak::tilDto).collect(Collectors.toSet());
    }

    @Path("/annenPartVedtak")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @BeskyttetRessurs(actionType = ActionType.READ, resourceType = ResourceType.UTTAKSPLAN)
    public AnnenPartVedtak annenPartsVedtak(@Valid @NotNull AnnenPartVedtakRequest request) {
        LOG.info("Henter annen parts vedtak. Parametere {}", request);
        var vedtakOpt = annenPartVedtakTjeneste.hentFor(
                map(request.aktørId.aktørId),
                map(request.annenPartAktørId.aktørId),
                Optional.ofNullable(request.barnAktørId).map(a -> map(a.aktørId)).orElse(null),
                request.familiehendelse
        );
        if (vedtakOpt.isEmpty()) {
            return null;
        }
        var vedtak = vedtakOpt.get();
        LOG.info("Returnerer annen parts vedtak. Antall perioder {}", vedtak.perioder().size());
        var perioder = vedtak.perioder().stream().map(UttakPeriode::tilDto).toList();
        return new AnnenPartVedtak(perioder, vedtak.termindato(), vedtak.dekningsgrad().tilDto(), vedtak.antallBarn());
    }

    public static AktørId map(String aktørId) {
        return new AktørId(aktørId);
    }

    public record AnnenPartVedtakRequest(@Valid @NotNull AktørIdDto aktørId,
                                         @Valid @NotNull AktørAnnenPartDto annenPartAktørId,
                                         @Valid AktørIdBarnDto barnAktørId,
                                         LocalDate familiehendelse) implements AbacDto {

        @Override
        public AbacDataAttributter abacAttributter() {
            var abacDataAttributter = aktørId.abacAttributter()
                    .leggTil(annenPartAktørId.abacAttributter());
            if (barnAktørId != null) {
                abacDataAttributter = abacDataAttributter.leggTil(barnAktørId.abacAttributter());
            }
            return abacDataAttributter;
        }
    }

    public record AktørIdDto(@NotNull @Digits(integer = 19, fraction = 0) String aktørId) implements AbacDto {

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public AktørIdDto {
        }

        @Override
        public AbacDataAttributter abacAttributter() {
            return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.AKTØR_ID, aktørId);
        }

        @Override
        public String toString() {
            return mask(aktørId);
        }
    }

    public record AktørAnnenPartDto(@NotNull @Digits(integer = 19, fraction = 0) String aktørId) implements AbacDto {


        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public AktørAnnenPartDto {
        }

        @Override
        public AbacDataAttributter abacAttributter() {
            return AbacDataAttributter.opprett().leggTil(ANNEN_PART, aktørId());
        }

        @Override
        public String toString() {
            return mask(aktørId);
        }

    }

    public record AktørIdBarnDto(@NotNull @Digits(integer = 19, fraction = 0) String aktørId) implements AbacDto {

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public AktørIdBarnDto {
        }

        @Override
        public AbacDataAttributter abacAttributter() {
            return AbacDataAttributter.opprett();
        }

        @Override
        public String toString() {
            return mask(aktørId);
        }
    }
}
