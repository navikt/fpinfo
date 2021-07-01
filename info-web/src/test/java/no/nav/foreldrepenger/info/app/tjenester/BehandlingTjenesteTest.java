package no.nav.foreldrepenger.info.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.app.ResourceLink;
import no.nav.foreldrepenger.info.app.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.BehandlingIdDto;
import no.nav.foreldrepenger.info.datatyper.BehandlingTema;
import no.nav.foreldrepenger.info.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.datatyper.FamilieHendelseType;
import no.nav.foreldrepenger.info.repository.Repository;

@ExtendWith(MockitoExtension.class)
class BehandlingTjenesteTest {
    private static final Long BEHANDLING_ID = 123L;
    private static final String BEHANDLING_STATUS = "FVED";
    private static final String FAGSAK_YTELSE_TYPE = FagsakYtelseType.FP.name();
    private static final String BEHANDLENDE_ENHET_KODE = "4082";
    private static final String BEHANDLENDE_ENHET_NAVN = "NAV";
    private static final String SAKSNUMMER = "12345";
    private static final String FAMILIE_HENDELSE_FØDSEL = FamilieHendelseType.FØDSEL.getVerdi();
    private static final String BEHANDLING_TEMA = BehandlingTema.FORELDREPENGER_FØDSEL;
    private static final String XML_CLOB = "xml clob";
    private static final String DOKUMENT_ID = "1234";
    private static final String JOURNALPOST_ID = "1234";
    private static final String LINK_PATH_SØKNAD = "/søknad?param=";
    @Mock
    private Repository mockRepository;
    private BehandlingTjeneste behandlingTjeneste;

    @BeforeEach
    void beforeEach() {
        behandlingTjeneste = new BehandlingTjeneste(new SøknadTjeneste(mockRepository), mockRepository);
    }

    @Test
    void skalKonvertereBehandlingTilDtoOgUtledeBehandlingTema() {
        when(mockRepository.hentBehandling(BEHANDLING_ID)).thenReturn(lagBehandling());
        when(mockRepository.hentMottattDokument(BEHANDLING_ID)).thenReturn(lagDokument());

        BehandlingDto dto = behandlingTjeneste.hentBehandling(new BehandlingIdDto(BEHANDLING_ID.toString()),
                LINK_PATH_SØKNAD);
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
    void skalIkkeLageLenkerSøknadSomIkkeFinnes() {
        when(mockRepository.hentBehandling(BEHANDLING_ID)).thenReturn(lagBehandling());
        when(mockRepository.hentMottattDokument(BEHANDLING_ID)).thenReturn(Collections.emptyList());

        BehandlingDto dto = behandlingTjeneste.hentBehandling(new BehandlingIdDto(BEHANDLING_ID.toString()),
                LINK_PATH_SØKNAD);

        assertThat(dto.getLenker()).isEmpty();
    }

    @Test
    void skalIkkeLageLenkeTilSøknadSomIkkeErRelevant() {
        when(mockRepository.hentBehandling(BEHANDLING_ID)).thenReturn(lagBehandling());
        when(mockRepository.hentMottattDokument(BEHANDLING_ID)).thenReturn(
                lagDokument(DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD));

        BehandlingDto dto = behandlingTjeneste.hentBehandling(new BehandlingIdDto(BEHANDLING_ID.toString()),
                LINK_PATH_SØKNAD);
        assertThat(dto.getLenker()).isEmpty();
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

    private static List<MottattDokument> lagDokument() {
        return lagDokument(DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL);
    }

    private static List<MottattDokument> lagDokument(DokumentTypeId type) {
        return List.of(dokumentBuilder().medForsendelseId(UUID.randomUUID()).medType(type.name()).build());
    }

    private static MottattDokument.Builder dokumentBuilder() {
        return MottattDokument.builder()
                .medBehandlingStatus(BEHANDLING_STATUS)
                .medJournalpostId(JOURNALPOST_ID)
                .medMottattDokumentId(DOKUMENT_ID)
                .medSøknadXml(XML_CLOB);
    }
}
