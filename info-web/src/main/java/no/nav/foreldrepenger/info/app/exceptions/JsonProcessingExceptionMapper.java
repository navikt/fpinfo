package no.nav.foreldrepenger.info.app.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.nav.vedtak.exception.TekniskException;

public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonProcessingException.class);

    @Override
    public Response toResponse(JsonProcessingException ex) {
        var e = new TekniskException("FP-299955", String.format("JSON-processing feil: %s", ex.getMessage()), ex);
        LOG.warn(e.getMessage(), e);
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new FeilDto(e.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
