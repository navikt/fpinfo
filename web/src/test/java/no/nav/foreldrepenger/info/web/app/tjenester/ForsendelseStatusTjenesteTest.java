package no.nav.foreldrepenger.info.web.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.felles.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.felles.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.felles.datatyper.FamilieHendelseType;
import no.nav.foreldrepenger.info.repository.Repository;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.ForsendelseIdDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.ForsendelseStatus;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.ForsendelseStatusDto;
import no.nav.vedtak.exception.TekniskException;

@ExtendWith(MockitoExtension.class)
public class ForsendelseStatusTjenesteTest {
    private static final Long BEHANDLING_ID = 123L;
    private static final String BEHANDLING_STATUS = "FVED";
    private static final String FAGSAK_YTELSE_TYPE = FagsakYtelseType.FP.getVerdi();
    private static final String BEHANDLENDE_ENHET_KODE = "4082";
    private static final String BEHANDLENDE_ENHET_NAVN = "NAV";
    private static final String SAKSNUMMER = "12345";
    private static final String FAMILIE_HENDELSE_FØDSEL = FamilieHendelseType.FØDSEL.getVerdi();
    private static final String XML_CLOB = "xml clob";
    private static final String DOKUMENT_ID = "1234";
    private static final String JOURNALPOST_ID = "1234";
    @Mock
    private Repository mockRepository;
    private ForsendelseStatusTjeneste forsendelseStatusTjeneste;

    @BeforeEach
    public void beforeEach() {
        forsendelseStatusTjeneste = new ForsendelseStatusTjeneste(mockRepository);
    }

    @Test
    public void skalReturnereTomtResultat() {
        UUID fid = UUID.randomUUID();

        when(mockRepository.hentMottatteDokumenter(any(UUID.class))).thenReturn(lagDokumenter(fid, 0, true));

        Optional<ForsendelseStatusDto> opt = forsendelseStatusTjeneste.hentForsendelseStatus(new ForsendelseIdDto(fid.toString()));

        assertThat(opt).isEmpty();
    }

    @Test
    public void skalReturnereMottatNårBehandlingForForsendelseIkkeFinnes() {
        UUID fid = UUID.randomUUID();

        when(mockRepository.hentMottatteDokumenter(any(UUID.class))).thenReturn(lagDokumenter(fid, 1, false));

        Optional<ForsendelseStatusDto> opt = forsendelseStatusTjeneste.hentForsendelseStatus(new ForsendelseIdDto(fid.toString()));

        assertThat(opt.orElseThrow().getForsendelseStatus()).isEqualTo(ForsendelseStatus.MOTTATT);
    }

    @Test
    public void skalReturnereStatusInformasjon() {
        UUID fid = UUID.randomUUID();

        when(mockRepository.hentMottatteDokumenter(any(UUID.class))).thenReturn(lagDokumenter(fid, 1, true));
        when(mockRepository.hentBehandling(anyLong())).thenReturn(lagBehandling());

        Optional<ForsendelseStatusDto> opt = forsendelseStatusTjeneste.hentForsendelseStatus(new ForsendelseIdDto(fid.toString()));

        assertThat(opt).isPresent();
        assertThat(opt.orElseThrow().getForsendelseStatus()).isEqualTo(ForsendelseStatus.PÅGÅR);
    }

    @Test
    public void skalKasteFeilVedFlereBehandlingerKnyttetTilForsendelseId() {
        UUID fid = UUID.randomUUID();

        when(mockRepository.hentMottatteDokumenter(any(UUID.class))).thenReturn(lagDokumenter(fid, 2, true));

        Assertions.assertThrows(TekniskException.class,
                () -> forsendelseStatusTjeneste.hentForsendelseStatus(new ForsendelseIdDto(fid.toString())));
    }

    private static Behandling lagBehandling() {
        return Behandling.builder()
                .medBehandlingId(BEHANDLING_ID)
                .medBehandlingStatus(BEHANDLING_STATUS)
                .medBehandlingResultatType(null)
                .medFagsakYtelseType(FAGSAK_YTELSE_TYPE)
                .medSaksnummer(SAKSNUMMER)
                .medFamilieHendelseType(FAMILIE_HENDELSE_FØDSEL)
                .medBehandlendeEnhet(BEHANDLENDE_ENHET_KODE, BEHANDLENDE_ENHET_NAVN)
                .build();
    }

    private static List<MottattDokument> lagDokumenter(UUID forsendelseId, int antall, boolean medBehandling) {
        List<MottattDokument> dokumenter = new ArrayList<>();
        while (antall > 0) {
            antall--;
            var builder = dokumentBuilder()
                    .medForsendelseId(forsendelseId)
                    .medType(DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL.getVerdi());
            if (medBehandling) {
                builder.medBehandlingId(BEHANDLING_ID + antall);
            }
            dokumenter.add(builder.build());
        }
        return dokumenter;
    }

    private static MottattDokument.Builder dokumentBuilder() {
        return MottattDokument.builder()
                .medBehandlingStatus(BEHANDLING_STATUS)
                .medJournalpostId(JOURNALPOST_ID)
                .medMottattDokumentId(DOKUMENT_ID)
                .medSøknadXml(XML_CLOB);
    }
}
