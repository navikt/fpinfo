package no.nav.foreldrepenger.info.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingTema;
import no.nav.foreldrepenger.info.felles.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.felles.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.felles.datatyper.FamilieHendelseType;
import no.nav.foreldrepenger.info.felles.rest.ResourceLink;
import no.nav.foreldrepenger.info.repository.DokumentForsendelseRepository;
import no.nav.foreldrepenger.info.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.tjenester.dto.BehandlingIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseStatus;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseStatusDataDTO;
import no.nav.vedtak.exception.TekniskException;

public class DokumentforsendelseTjenesteImplTest {
    private static final Long BEHANDLING_ID = 123L;
    private static final String BEHANDLING_STATUS = "FVED";
    private static final String FAGSAK_YTELSE_TYPE = FagsakYtelseType.FP.getVerdi();
    private static final String BEHANDLENDE_ENHET_KODE = "4082";
    private static final String BEHANDLENDE_ENHET_NAVN = "NAV";
    private static final String SAKSNUMMER = "12345";
    private static final String FAMILIE_HENDELSE_FØDSEL = FamilieHendelseType.FØDSEL.getVerdi();
    private static final String BEHANDLING_TEMA = BehandlingTema.FORELDREPENGER_FØDSEL;
    private static final String XML_CLOB = "xml clob";
    private static final String DOKUMENT_ID = "1234";
    private static final String JOURNALPOST_ID = "1234";
    private static final String LINK_PATH_SØKNAD = "/søknad?param=";
    private static final Long UKJENT_BEHANDLING_ID = 987L;

    private DokumentforsendelseTjeneste tjeneste;

    @Mock
    private DokumentForsendelseRepository mockRepository = mock(DokumentForsendelseRepository.class);

    @BeforeEach
    public void setUp() {
        tjeneste = new DokumentforsendelseTjenesteImpl(mockRepository);

        when(mockRepository.hentBehandling(UKJENT_BEHANDLING_ID))
                .thenThrow(DokumentforsendelseTjenesteImpl.DokumentforsendelseFeil.FACTORY
                        .fantIkkeSøknadForBehandling(UKJENT_BEHANDLING_ID).toException());
    }

    @Test
    public void skalKonvertereBehandlingTilDtoOgUtledeBehandlingTema() {
        when(mockRepository.hentBehandling(BEHANDLING_ID)).thenReturn(lagBehandling());
        when(mockRepository.harSøknad(BEHANDLING_ID)).thenReturn(true);

        BehandlingDto dto = tjeneste.hentBehandling(new BehandlingIdDto(BEHANDLING_ID.toString()), LINK_PATH_SØKNAD);
        assertThat(dto.getType()).isEqualTo(FAGSAK_YTELSE_TYPE);
        assertThat(dto.getBehandlendeEnhet()).isEqualTo(BEHANDLENDE_ENHET_KODE);
        assertThat(dto.getBehandlendeEnhetNavn()).isEqualTo(BEHANDLENDE_ENHET_NAVN);
        assertThat(dto.getStatus()).isEqualTo(BEHANDLING_STATUS);
        assertThat(dto.getTema()).isEqualTo(BEHANDLING_TEMA);
        assertThat(dto.getLenker()).isNotEmpty();

        ResourceLink EXPECTED_SØKNAD_LINK = ResourceLink.get(LINK_PATH_SØKNAD + BEHANDLING_ID, "søknad", null);

        assertThat(dto.getLenker().get(0)).isEqualTo(EXPECTED_SØKNAD_LINK);
    }

    @Test
    public void skalIkkeLageLenkerSøknadSomIkkeFinnes() {
        when(mockRepository.hentBehandling(BEHANDLING_ID)).thenReturn(lagBehandling());
        when(mockRepository.harSøknad(BEHANDLING_ID)).thenReturn(false);

        BehandlingDto dto = tjeneste.hentBehandling(new BehandlingIdDto(BEHANDLING_ID.toString()), LINK_PATH_SØKNAD);

        assertThat(dto.getLenker()).isEmpty();
    }

    @Test
    public void skalReturnereTomtResultat() {
        UUID fid = UUID.randomUUID();

        when(mockRepository.hentMottatteDokumenter(any(UUID.class))).thenReturn(lagDokumenter(fid, 0, true));

        Optional<ForsendelseStatusDataDTO> opt = tjeneste.hentStatusInformasjon(new ForsendelseIdDto(fid.toString()));

        assertThat(opt).isEmpty();
    }

    @Test
    public void skalReturnereMottatNårBehandlingForForsendelseIkkeFinnes() {
        UUID fid = UUID.randomUUID();

        when(mockRepository.hentMottatteDokumenter(any(UUID.class))).thenReturn(lagDokumenter(fid, 1, false));

        Optional<ForsendelseStatusDataDTO> opt = tjeneste.hentStatusInformasjon(new ForsendelseIdDto(fid.toString()));

        assertThat(opt.get().getForsendelseStatus()).isEqualTo(ForsendelseStatus.MOTTATT);
    }

    @Test
    public void skalReturnereStatusInformasjon() {
        UUID fid = UUID.randomUUID();

        when(mockRepository.hentMottatteDokumenter(any(UUID.class))).thenReturn(lagDokumenter(fid, 1, true));
        when(mockRepository.hentBehandling(anyLong())).thenReturn(lagBehandling());

        Optional<ForsendelseStatusDataDTO> opt = tjeneste.hentStatusInformasjon(new ForsendelseIdDto(fid.toString()));

        assertThat(opt).isPresent();
        assertThat(opt.get().getForsendelseStatus()).isEqualTo(ForsendelseStatus.PÅGÅR);
    }

    @Test
    public void skalKasteFeilVedFlereBehandlingerKnyttetTilForsendelseId() {
        UUID fid = UUID.randomUUID();

        when(mockRepository.hentMottatteDokumenter(any(UUID.class))).thenReturn(lagDokumenter(fid, 2, true));

        Assertions.assertThrows(TekniskException.class,
                () -> tjeneste.hentStatusInformasjon(new ForsendelseIdDto(fid.toString())));
    }

    private Behandling lagBehandling() {
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

    private List<MottattDokument> lagDokumenter(UUID forsendelseId, int antall, boolean medBehandling) {
        List<MottattDokument> dokumenter = new ArrayList<>();
        while (antall > 0) {
            antall--;
            MottattDokument.Builder builder = MottattDokument.builder()
                    .medBehandlingStatus(BEHANDLING_STATUS)
                    .medForsendelseId(forsendelseId)
                    .medJournalpostId(JOURNALPOST_ID)
                    .medMottattDokumentId(DOKUMENT_ID)
                    .medSøknadXml(XML_CLOB)
                    .medType(DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL.getVerdi());
            if (medBehandling) {
                builder.medBehandlingId(BEHANDLING_ID + antall);
            }
            dokumenter.add(builder.build());
        }
        return dokumenter;
    }
}
