package no.nav.foreldrepenger.info.web.app.tjenester;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.foreldrepenger.info.web.abac.BeskyttetRessursAttributt.FAGSAK;
import static no.nav.foreldrepenger.info.web.abac.BeskyttetRessursAttributt.UTTAKSPLAN;
import static no.nav.foreldrepenger.info.web.app.tjenester.DokumentforsendelseTjeneste.DOKUMENTFORSENDELSE_PATH;
import static no.nav.foreldrepenger.info.web.server.JettyServer.ACR_LEVEL4;
import static no.nav.foreldrepenger.info.web.server.JettyServer.TOKENX;
import static no.nav.foreldrepenger.sikkerhet.abac.domene.ActionType.READ;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.AktørAnnenPartDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.AktørIdDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.BehandlingIdDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.ForsendelseIdDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.ForsendelseStatusDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.SakDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.SaksnummerDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.SøknadXmlDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.SøknadsGrunnlagDto;
import no.nav.foreldrepenger.sikkerhet.abac.BeskyttetRessurs;
import no.nav.security.token.support.core.api.ProtectedWithClaims;

@Path(DOKUMENTFORSENDELSE_PATH)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
@ProtectedWithClaims(issuer = TOKENX, claimMap = { ACR_LEVEL4 })
public class DokumentforsendelseTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(DokumentforsendelseTjeneste.class);

    static final String DOKUMENTFORSENDELSE_PATH = "/dokumentforsendelse";
    private static final String API_PATH = "/fpinfo/api" + DOKUMENTFORSENDELSE_PATH;
    private static final String BEHANDLING_PATH = "/behandling";
    private static final String STATUS_PATH = "/status";
    private static final String SAK_PATH = "/sak";
    private static final String SOKNAD_PATH = "/soknad";
    private static final String UTTAKSPLAN_PATH = "/uttaksplan";
    protected static final String ANNENFORELDERPLAN_PATH = "/annenforelderplan";

    private BehandlingTjeneste behandling;
    private SøknadTjeneste søknad;
    private ForsendelseStatusTjeneste forsendelse;
    private SøknadsGrunnlagTjeneste grunnlag;
    private SakTjeneste sak;

    @Inject
    public DokumentforsendelseTjeneste(BehandlingTjeneste behandling,
            SøknadTjeneste søknad,
            ForsendelseStatusTjeneste forsendelse,
            SøknadsGrunnlagTjeneste grunnlag,
            SakTjeneste sak) {
        this.behandling = behandling;
        this.søknad = søknad;
        this.forsendelse = forsendelse;
        this.grunnlag = grunnlag;
        this.sak = sak;
    }

    public DokumentforsendelseTjeneste() {
    }

    @GET
    @Path(BEHANDLING_PATH)
    @BeskyttetRessurs(action = READ, resource = FAGSAK, path = DOKUMENTFORSENDELSE_PATH + BEHANDLING_PATH)
    @Operation(description = "Url for å hente Behandling", summary = "Returnerer Behandling", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = BehandlingDto.class)), responseCode = "200", description = "Returnerer søknad XML")
    })
    public BehandlingDto hentBehandling(
            @NotNull @QueryParam("behandlingId") @Parameter(name = "behandlingId") @Valid BehandlingIdDto id) {
        return behandling.hentBehandling(id, API_PATH + "/soknad?behandlingId=");
    }

    @GET
    @Path(STATUS_PATH)
    @BeskyttetRessurs(action = READ, resource = FAGSAK, path = DOKUMENTFORSENDELSE_PATH + STATUS_PATH)
    @Operation(description = "Søker om status på prossesseringen av et mottatt dokument", summary = "status på prossesseringen av et mottatt dokument", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForsendelseStatusDto.class)), responseCode = "200", description = "Status og Periode"),
            @ApiResponse(responseCode = "404", description = "Forsendelse ikke funnet")
    })
    public Response getForsendelseStatus(
            @NotNull @QueryParam("forsendelseId") @Parameter(name = "forsendelseId") @Valid ForsendelseIdDto id) {

        return forsendelse.hentForsendelseStatus(id)
                .map(d -> ok(d).build())
                .orElseGet(() -> status(NOT_FOUND).build());
    }

    @GET
    @Path(SAK_PATH)
    @BeskyttetRessurs(action = READ, resource = FAGSAK, path = DOKUMENTFORSENDELSE_PATH + SAK_PATH)
    @Operation(description = "Url for å hente sak status informasjon som er relevant til aktør", summary = ("Returnerer Sak Status Informasjon"), responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = SakDto.class)), responseCode = "200", description = "Returnerer Sak Status Informasjon")
    })
    public List<SakDto> hentSak(
            @NotNull @QueryParam("aktorId") @Parameter(name = "aktorId") @Valid AktørIdDto aktørIdDto) {
        return sak.hentSak(aktørIdDto, API_PATH + "/behandling?behandlingId=", API_PATH + "/uttaksplan?saksnummer=");
    }

    @GET
    @Path(SOKNAD_PATH)
    @BeskyttetRessurs(action = READ, resource = FAGSAK, path = DOKUMENTFORSENDELSE_PATH + SOKNAD_PATH)
    @Operation(description = "Url for å hente søknad XML og journalpostId", summary = "Returnerer søknad XML og Journalpost ID", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = SøknadXmlDto.class)), responseCode = "200", description = "Returnerer søknad XML")
    })
    public Response hentSøknadXml(
            @NotNull @QueryParam("behandlingId") @Parameter(name = "behandlingId") @Valid BehandlingIdDto id) {
        return søknad.hentSøknadXml(id.getBehandlingId())
                .map(xml -> ok(xml).build())
                .orElseGet(() -> status(NOT_FOUND).build());
    }

    @GET
    @Path(UTTAKSPLAN_PATH)
    @BeskyttetRessurs(action = READ, resource = UTTAKSPLAN, path = DOKUMENTFORSENDELSE_PATH + UTTAKSPLAN_PATH)
    @Operation(description = "Url for å hente søknadsgrunnlag og felles uttaksplan", summary = "Returnerer grunnlagsinfo og liste av uttaksperioder for begge parter", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = SøknadsGrunnlagDto.class)), responseCode = "200", description = "Returnerer søknad XML")
    })
    public Response hentSøknadsgrunnlag(
            @NotNull @QueryParam("saksnummer") @Parameter(name = "saksnummer") @Valid SaksnummerDto saksnummer) {
        return grunnlag.hentSøknadsgrunnlag(saksnummer, false)
                .map(d -> ok(d).build())
                .orElse(noContent().build());
    }

    @GET
    @Path(ANNENFORELDERPLAN_PATH)
    @BeskyttetRessurs(action = READ, resource = UTTAKSPLAN, path = DOKUMENTFORSENDELSE_PATH + ANNENFORELDERPLAN_PATH)
    @Operation(description = "Url for å hente søknadsgrunnlag og felles uttaksplan", summary = "Returnerer grunnlagsinfo og liste av uttaksperioder for begge parter", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = SøknadsGrunnlagDto.class)), responseCode = "200", description = "Returnerer søknad XML")
    })
    public Response hentGrunnlagForAnnenPart(
            @NotNull @QueryParam("aktorIdBruker") @Parameter(name = "aktorId") @Valid AktørIdDto bruker,
            @NotNull @QueryParam("aktorIdAnnenPart") @Parameter(name = "aktorId") @Valid AktørAnnenPartDto annenPart) {
        return grunnlag.hentSøknadAnnenPart(bruker, annenPart)
                .map(d -> ok(d).build())
                .orElse(noContent().build());
    }

}
