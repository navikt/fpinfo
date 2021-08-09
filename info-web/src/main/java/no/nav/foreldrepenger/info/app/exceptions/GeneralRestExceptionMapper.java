package no.nav.foreldrepenger.info.app.exceptions;

import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.log.util.LoggerUtils;

@Provider
public class GeneralRestExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(GeneralRestExceptionMapper.class);

    @Override
    public Response toResponse(Throwable cause) {
        loggTilApplikasjonslogg(cause);
        String callId = MDCOperations.getCallId();

        if (cause instanceof VLException c) {
            return handleVLException(c, callId);
        }

        return handleGenerellFeil(cause, callId);
    }

    private static Response handleValideringsfeil(ValideringsfeilException valideringsfeil) {
        List<String> feltNavn = valideringsfeil.getFeltFeil().stream().map(FeltFeilDto::navn)
                .toList();
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new FeilDto(
                        FeltValideringFeil.feltverdiKanIkkeValideres(feltNavn).getMessage(),
                        valideringsfeil.getFeltFeil()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

        if (cause instanceof ManglerTilgangException m) {
            return ikkeTilgang(m);
        }
        return serverError(callId, vlException);
    }


    private static Response serverError(Throwable feil) {
        String feilmelding = getVLExceptionFeilmelding(feil);
        return Response.serverError()
                .entity(new FeilDto(feilmelding, FeilType.GENERELL_FEIL))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private static Response ikkeTilgang(ManglerTilgangException feil) {
        String feilmelding = feil.getMessage();
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new FeilDto(feilmelding, FeilType.MANGLER_TILGANG_FEIL))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private static String getVLExceptionFeilmelding(Throwable feil) {
        var callId = MDCOperations.getCallId();
        String feilbeskrivelse = getExceptionMelding(feil);
        if (feil instanceof FunksjonellException f) {
            String løsningsforslag = f.getLøsningsforslag();
            return "Det oppstod en feil: "
                    + avsluttMedPunktum(feilbeskrivelse)
                    + avsluttMedPunktum(løsningsforslag)
                    + ". Referanse-id: " + callId;
        }
        return "Det oppstod en serverfeil: "
                + avsluttMedPunktum(feilbeskrivelse)
                + ". Meld til support med referanse-id: " + callId;

    }

    private static String avsluttMedPunktum(String tekst) {
        return tekst + (tekst.endsWith(".") ? " " : ". ");
    }

    private static void loggTilApplikasjonslogg(Throwable cause) {
        var feil = getExceptionMelding(cause);
        if (cause instanceof ManglerTilgangException) {
            LOG.info(feil, cause);
        } else {
            LOG.error("Fikk uventet feil:" + feil, cause);
        }
        // key for å tracke prosess -- nullstill denne
        MDC.remove("prosess");
    }

    private static String getExceptionMelding(Throwable feil) {
        return getTextForField(feil.getMessage());
    }

    private static String getTextForField(String input) {
        return input != null ? LoggerUtils.removeLineBreaks(input) : "";
    }
}
