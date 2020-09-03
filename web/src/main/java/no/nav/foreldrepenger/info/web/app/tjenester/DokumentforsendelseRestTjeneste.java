package no.nav.foreldrepenger.info.web.app.tjenester;

import static no.nav.foreldrepenger.info.web.app.tjenester.DokumentforsendelseRestTjeneste.DOKUMENTFORSENDELSE_PATH;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.UTTAKSPLAN;

import java.util.List;
import java.util.Optional;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import no.nav.foreldrepenger.info.tjenester.DokumentforsendelseTjeneste;
import no.nav.foreldrepenger.info.tjenester.dto.AktørAnnenPartDto;
import no.nav.foreldrepenger.info.tjenester.dto.AktørIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.tjenester.dto.BehandlingIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseStatusDataDTO;
import no.nav.foreldrepenger.info.tjenester.dto.SakStatusDto;
import no.nav.foreldrepenger.info.tjenester.dto.SaksnummerDto;
import no.nav.foreldrepenger.info.tjenester.dto.SøknadXmlDto;
import no.nav.foreldrepenger.info.tjenester.dto.SøknadsGrunnlagDto;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Path(DOKUMENTFORSENDELSE_PATH)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class DokumentforsendelseRestTjeneste {

    public static final String DOKUMENTFORSENDELSE_PATH = "/dokumentforsendelse";
    private static final String API_PATH = "/fpinfo/api" + DOKUMENTFORSENDELSE_PATH;

    private DokumentforsendelseTjeneste dokumentForsendelseTjenester;

    public DokumentforsendelseRestTjeneste() {
        // For Rest-CDI
    }

    @Inject
    public DokumentforsendelseRestTjeneste(DokumentforsendelseTjeneste dokumentForsendelseTjenester) {
        this.dokumentForsendelseTjenester = dokumentForsendelseTjenester;
    }

    @GET
    @Path("/behandling")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @Operation(description = "Url for å hente Behandling", summary = "Returnerer Behandling", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = BehandlingDto.class)), responseCode = "200", description = "Returnerer søknad XML")
    })
    public BehandlingDto hentBehandling(
            @NotNull @QueryParam("behandlingId") @Parameter(name = "behandlingId") @Valid BehandlingIdDto behandlingIdDto) {
        String linkPathSøknad = API_PATH + "/soknad?behandlingId=";
        return dokumentForsendelseTjenester.hentBehandling(behandlingIdDto, linkPathSøknad);
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @Operation(description = "Søker om status på prossesseringen av et mottatt dokument", summary = "status på prossesseringen av et mottatt dokument", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForsendelseStatusDataDTO.class)), responseCode = "200", description = "Status og Periode"),
            @ApiResponse(responseCode = "404", description = "Forsendelse ikke funnet")

    })
    public Response getStatusInformasjon(
            @NotNull @QueryParam("forsendelseId") @Parameter(name = "forsendelseId") @Valid ForsendelseIdDto forsendelseIdDto) {
        Optional<ForsendelseStatusDataDTO> dto = dokumentForsendelseTjenester.hentStatusInformasjon(forsendelseIdDto);
        return dto.map(forsendelseStatusDataDTO -> Response.ok(forsendelseStatusDataDTO).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/sak")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @Operation(description = "Url for å hente sak status informasjon som er relevant til aktør", summary = ("Returnerer Sak Status Informasjon"), responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = SakStatusDto.class)), responseCode = "200", description = "Returnerer Sak Status Informasjon")
    })
    public List<SakStatusDto> hentSakStatus(
            @NotNull @QueryParam("aktorId") @Parameter(name = "aktorId") @Valid AktørIdDto aktørIdDto) {
        String linkPathBehandling = API_PATH + "/behandling?behandlingId=";
        String linkPathUttaksplan = API_PATH + "/uttaksplan?saksnummer=";
        return dokumentForsendelseTjenester.hentSakStatus(aktørIdDto, linkPathBehandling, linkPathUttaksplan);
    }

    @GET
    @Path("/soknad")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @Operation(description = "Url for å hente søknad XML og journalpostId", summary = "Returnerer søknad XML og Journalpost ID", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = SøknadXmlDto.class)), responseCode = "200", description = "Returnerer søknad XML")
    })
    public Response hentSøknadXml(
            @NotNull @QueryParam("behandlingId") @Parameter(name = "behandlingId") @Valid BehandlingIdDto behandlingIdDto) {
        return dokumentForsendelseTjenester.hentSøknadXml(behandlingIdDto)
                .map(søknadXml -> Response.ok(søknadXml).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/uttaksplan")
    @BeskyttetRessurs(action = READ, ressurs = UTTAKSPLAN)
    @Operation(description = "Url for å hente søknadsgrunnlag og felles uttaksplan", summary = "Returnerer grunnlagsinfo og liste av uttaksperioder for begge parter", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = SøknadsGrunnlagDto.class)), responseCode = "200", description = "Returnerer søknad XML")
    })
    public Response hentSøknadsgrunnlag(
            @NotNull @QueryParam("saksnummer") @Parameter(name = "saksnummer") @Valid SaksnummerDto saksnummerDto) {
        var dto = dokumentForsendelseTjenester.hentSøknadsgrunnlag(saksnummerDto, false);
        return dto.map(d -> Response.ok(d).build()).orElse(Response.noContent().build());
    }

    @GET
    @Path("/annenforelderplan")
    @BeskyttetRessurs(action = READ, ressurs = UTTAKSPLAN)
    @Operation(description = "Url for å hente søknadsgrunnlag og felles uttaksplan", summary = "Returnerer grunnlagsinfo og liste av uttaksperioder for begge parter", responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = SøknadsGrunnlagDto.class)), responseCode = "200", description = "Returnerer søknad XML")
    })
    public Response hentGrunnlagForAnnenPart(
            @NotNull @QueryParam("aktorIdBruker") @Parameter(name = "aktorId") @Valid AktørIdDto aktørIdBrukerDto,
            @NotNull @QueryParam("aktorIdAnnenPart") @Parameter(name = "aktorId") @Valid AktørAnnenPartDto aktørAnnenPartDto) {
        var søknadsGrunnlagDto = dokumentForsendelseTjenester.hentSøknadAnnenPart(aktørIdBrukerDto, aktørAnnenPartDto);
        return søknadsGrunnlagDto.map(d -> Response.ok(d).build()).orElse(Response.noContent().build());
    }

}
