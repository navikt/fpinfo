package no.nav.foreldrepenger.info.app.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;

class JsonMappingExceptionMapperTest {

    @Test
    void skal_mappe_InvalidTypeIdException() {
        var mapper = new JsonProcessingExceptionMapper();
        Response resultat = mapper.toResponse(new InvalidTypeIdException(null, "Ukjent type-kode", null, "23525"));
        FeilDto dto = (FeilDto) resultat.getEntity();
        assertThat(dto.feilmelding()).contains("JSON-processing feil");
        assertThat(dto.feltFeil()).isEmpty();
    }
}
