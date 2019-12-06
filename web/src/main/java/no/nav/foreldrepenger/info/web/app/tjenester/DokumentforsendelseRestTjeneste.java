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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = {"dokumentforsendelse"})
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
    @ApiOperation(value = "Url for å hente Behandling", notes = "Returnerer Behandling")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returnerer Status", response = BehandlingDto.class)})
    public BehandlingDto hentBehandling(@NotNull @QueryParam("behandlingId") @ApiParam("behandlingId") @Valid BehandlingIdDto behandlingIdDto) {
        String linkPathVedtak = API_PATH + "/vedtak?behandlingId=";
        String linkPathSøknad = API_PATH + "/soknad?behandlingId=";
        return dokumentForsendelseTjenester.hentBehandling(behandlingIdDto, linkPathSøknad);
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @ApiOperation(value = "Søker om status på prossesseringen av et mottatt dokument",
            notes = "status på prossesseringen av et mottatt dokument")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Status og Periode",
                    response = ForsendelseStatusDataDTO.class
            ),
            @ApiResponse(code = 404, message = "Forsendelse ikke funnet")
    })
    public Response getStatusInformasjon(@NotNull @QueryParam("forsendelseId") @ApiParam("forsendelseId") @Valid ForsendelseIdDto forsendelseIdDto) {
        Optional<ForsendelseStatusDataDTO> dto = dokumentForsendelseTjenester.hentStatusInformasjon(forsendelseIdDto);
        return dto.map(forsendelseStatusDataDTO -> Response.ok(forsendelseStatusDataDTO).build()).orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }


    @GET
    @Path("/sak")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @ApiOperation(value = "Url for å hente sak status informasjon som er relevant til aktør", notes = ("Returnerer Sak Status Informasjon"))
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returnerer Sak Status Informasjon", response = SakStatusDto.class)})
    public List<SakStatusDto> hentSakStatus(@NotNull @QueryParam("aktorId") @ApiParam("aktorId") @Valid AktørIdDto aktørIdDto) {
        String linkPathBehandling = API_PATH + "/behandling?behandlingId=";
        String linkPathUttaksplan = API_PATH + "/uttaksplan?saksnummer=";
        return dokumentForsendelseTjenester.hentSakStatus(aktørIdDto, linkPathBehandling, linkPathUttaksplan);
    }

    @GET
    @Path("/soknad")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @ApiOperation(value = "Url for å hente søknad XML og journalpostId", notes = "Returnerer søknad XML og Journalpost ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returnerer søknad XML", response = SøknadXmlDto.class)
    })
    public SøknadXmlDto hentSøknadXml(@NotNull @QueryParam("behandlingId") @ApiParam("behandlingId") @Valid BehandlingIdDto behandlingIdDto) {
        return dokumentForsendelseTjenester.hentSøknadXml(behandlingIdDto);
    }

    @GET
    @Path("/uttaksplan")
    @BeskyttetRessurs(action = READ, ressurs = UTTAKSPLAN)
    @ApiOperation(value = "Url for å hente søknadsgrunnlag og felles uttaksplan", notes = "Returnerer grunnlagsinfo og liste av uttaksperioder for begge parter")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "TODO", response = SøknadsGrunnlagDto.class)
    })
    public SøknadsGrunnlagDto hentSøknadsgrunnlag(@NotNull @QueryParam("saksnummer") @ApiParam("saksnummer") @Valid SaksnummerDto saksnummerDto) {
        return dokumentForsendelseTjenester.hentSøknadsgrunnlag(saksnummerDto, false);
    }

    @GET
    @Path("/annenforelderplan")
    @BeskyttetRessurs(action = READ, ressurs = UTTAKSPLAN)
    @ApiOperation(value = "Url for å hente søknadsgrunnlag og felles uttaksplan", notes = "Returnerer grunnlagsinfo og liste av uttaksperioder for annen part")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "TODO", response = SøknadsGrunnlagDto.class)
    })
    public SøknadsGrunnlagDto hentGrunnlagForAnnenPart(
            @NotNull @QueryParam("aktorIdBruker") @ApiParam("aktorId") @Valid AktørIdDto aktørIdBrukerDto,
            @NotNull @QueryParam("aktorIdAnnenPart") @ApiParam("aktorId") @Valid AktørAnnenPartDto aktørAnnenPartDto) {
        return dokumentForsendelseTjenester.hentSøknadAnnenPart(aktørIdBrukerDto, aktørAnnenPartDto);
    }
}
