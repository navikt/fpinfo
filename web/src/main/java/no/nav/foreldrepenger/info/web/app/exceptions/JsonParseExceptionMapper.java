package no.nav.foreldrepenger.info.web.app.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;

import no.nav.vedtak.exception.TekniskException;

public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    private static final Logger log = LoggerFactory.getLogger(JsonParseExceptionMapper.class);

    @Override
    public Response toResponse(JsonParseException ex) {
        var e = new TekniskException("FP-299955", String.format("JSON-parsing feil: %s", ex.getMessage()), ex);
        e.log(log);
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new FeilDto(e.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
