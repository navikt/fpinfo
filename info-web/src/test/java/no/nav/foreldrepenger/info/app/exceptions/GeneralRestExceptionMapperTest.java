package no.nav.foreldrepenger.info.app.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.jboss.resteasy.spi.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.app.exceptions.FeilDto;
import no.nav.foreldrepenger.info.app.exceptions.FeilType;
import no.nav.foreldrepenger.info.app.exceptions.FeltFeilDto;
import no.nav.foreldrepenger.info.app.exceptions.GeneralRestExceptionMapper;
import no.nav.foreldrepenger.info.app.exceptions.ValideringsfeilException;
import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.TekniskException;

class GeneralRestExceptionMapperTest {

    private GeneralRestExceptionMapper generalRestExceptionMapper;

    @BeforeEach
    void setUp() {
        generalRestExceptionMapper = new GeneralRestExceptionMapper();
    }

    @Test
    void skalMappeValideringsfeil() {
        var feltFeilDto = new FeltFeilDto("feltnavn", "En feilmelding");
        var valideringsfeil = new ValideringsfeilException(List.of(feltFeilDto));
        var response = generalRestExceptionMapper.toResponse(new ApplicationException(valideringsfeil));

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        FeilDto feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains(
                "Det oppstod en valideringsfeil på felt [feltnavn]. Vennligst kontroller at alle feltverdier er korrekte.");
        assertThat(feilDto.feltFeil()).hasSize(1);
        assertThat(feilDto.feltFeil().iterator().next()).isEqualTo(feltFeilDto);
    }

    @Test
    void skalMapperValideringsfeilMedMetainformasjon() {
        var feltFeilDto = new FeltFeilDto("feltnavn", "feilmelding");
        var valideringsfeil = new ValideringsfeilException(List.of(feltFeilDto));
        var response = generalRestExceptionMapper.toResponse(new ApplicationException(valideringsfeil));

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains(
                "Det oppstod en valideringsfeil på felt [feltnavn]. Vennligst kontroller at alle feltverdier er korrekte.");
        assertThat(feilDto.feltFeil()).hasSize(1);
        assertThat(feilDto.feltFeil().iterator().next()).isEqualTo(feltFeilDto);
    }

    @Test
    void skalMappeManglerTilgangFeil() {
        var manglerTilgangFeil = TestFeil.manglerTilgangFeil();
        var response = generalRestExceptionMapper.toResponse(new ApplicationException(manglerTilgangFeil));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.type()).isEqualTo(FeilType.MANGLER_TILGANG_FEIL);
        assertThat(feilDto.feilmelding()).contains("ManglerTilgangFeilmeldingKode");
    }

    @Test
    void skalMappeFunksjonellFeil() {
        var funksjonellFeil = TestFeil.funksjonellFeil();

        var response = generalRestExceptionMapper.toResponse(new ApplicationException(funksjonellFeil));

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains("FUNK_FEIL");
        assertThat(feilDto.feilmelding()).contains("en funksjonell feilmelding");
        assertThat(feilDto.feilmelding()).contains("et løsningsforslag");
    }

    @Test
    void skalMappeVLException() {
        var vlException = TestFeil.tekniskFeil();
        var response = generalRestExceptionMapper.toResponse(new ApplicationException(vlException));

        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains("TEK_FEIL");
        assertThat(feilDto.feilmelding()).contains("en teknisk feilmelding");
    }

    @Test
    void skalMappeGenerellFeil() {
        String feilmelding = "en helt generell feil";
        var generellFeil = new RuntimeException(feilmelding);
        var response = generalRestExceptionMapper.toResponse(new ApplicationException(generellFeil));

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity()).isInstanceOf(FeilDto.class);
        var feilDto = (FeilDto) response.getEntity();

        assertThat(feilDto.feilmelding()).contains(feilmelding);
    }

    private static class TestFeil {

        static FunksjonellException funksjonellFeil() {
            return new FunksjonellException("FUNK_FEIL", "en funksjonell feilmelding", "et løsningsforslag");
        }

        static TekniskException tekniskFeil() {
            return new TekniskException("TEK_FEIL", "en teknisk feilmelding");

        }

        static ManglerTilgangException manglerTilgangFeil() {
            return new ManglerTilgangException("MANGLER_TILGANG_FEIL", "ManglerTilgangFeilmeldingKode");
        }
    }
}
