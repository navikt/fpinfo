package no.nav.foreldrepenger.info.tjenester.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.domene.SakStatus;
import no.nav.foreldrepenger.info.domene.Saksnummer;
import no.nav.foreldrepenger.info.domene.UttakPeriode;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingResultatType;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingStatus;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingTema;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingÅrsak;
import no.nav.foreldrepenger.info.felles.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.felles.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.felles.datatyper.FamilieHendelseType;
import no.nav.foreldrepenger.info.felles.datatyper.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.info.felles.datatyper.MorsAktivitet;
import no.nav.foreldrepenger.info.felles.rest.ResourceLink;
import no.nav.vedtak.exception.TekniskException;

public class DomeneTilDtoTransformasjonTest {
    private static Long BEHANDLING_ID = 1234L;
    private static String SAKSNUMMER = "4567";
    private static String BEHANDLENDE_ENHET = "4052";
    private static String BEHANDLENDE_ENHET_NAVN = "NAV enhet";
    private static String RESULTAT_TYPE = BehandlingResultatType.IKKE_FASTSATT.getVerdi();
    private static String BEHANDLING_STATUS = BehandlingStatus.UTREDES.getVerdi();
    private static String FAGSAK_YTELSE_TYPE = FagsakYtelseType.FP.getVerdi();
    private static String BEHANDLING_ÅRSAK = BehandlingÅrsak.ANNET.getVerdi();
    private static String FAMILIEHENDELSE_TYPE = FamilieHendelseType.FØDSEL.getVerdi();
    private static String TEST_LENKE = "test-lenke";
    private static String TEST_REL = "test-rel";
    private static String FAGSAK_STATUS = "OPPR";
    private static String AKTØR_ID = "1234";
    private static String AKTØR_ID_ANNEN_PART = "5678";
    private static String JOURNALPOST_ID = "123456789";
    private static String XML_PAYLOAD = "xml";
    private static ResourceLink EXPECTED_RESOURCE_LINK = ResourceLink.get(TEST_LENKE, TEST_REL, null);
    String DOKUMENT_ID = "123";
    UUID FORSENDELSE_ID = UUID.randomUUID();
    String DOKUMENT_TYPE_ID = DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL.getVerdi();

    @Test
    public void skalTransformereBehandlingTilBehandlingDto() {
        Behandling behandling = Behandling.builder()
                .medBehandlingId(BEHANDLING_ID)
                .medBehandlingResultatType(RESULTAT_TYPE)
                .medBehandlingStatus(BEHANDLING_STATUS)
                .medBehandlendeEnhet(BEHANDLENDE_ENHET, BEHANDLENDE_ENHET_NAVN)
                .medFagsakYtelseType(FAGSAK_YTELSE_TYPE)
                .medBehandlingÅrsak(BEHANDLING_ÅRSAK)
                .medFamilieHendelseType(FAMILIEHENDELSE_TYPE)
                .medSaksnummer(SAKSNUMMER)
                .build();

        String forventetBehandlingTema = BehandlingTema.fraYtelse(FAGSAK_YTELSE_TYPE, FAMILIEHENDELSE_TYPE);

        BehandlingDto dto = BehandlingDto.fraDomene(behandling);
        assertThat(dto.getStatus()).isEqualTo(BEHANDLING_STATUS);
        assertThat(dto.getTema()).isEqualTo(forventetBehandlingTema);
        assertThat(dto.getType()).isEqualTo(FAGSAK_YTELSE_TYPE);
        assertThat(dto.getÅrsak()).isEqualTo(BEHANDLING_ÅRSAK);
        assertThat(dto.getBehandlendeEnhet()).isEqualTo(BEHANDLENDE_ENHET);
        assertThat(dto.getBehandlendeEnhetNavn()).isEqualTo(BEHANDLENDE_ENHET_NAVN);
        assertThat(dto.getLenker()).isEmpty();

        dto.leggTilLenke(TEST_LENKE, TEST_REL);

        assertThat(dto.getLenker()).hasSize(1);
        assertThat(dto.getLenker().get(0)).isEqualTo(EXPECTED_RESOURCE_LINK);
    }

    @Test
    public void skalTransformereSakStatusTilSakStatusDto() {
        SakStatus sakStatus = SakStatus.builder()
                .medFagsakStatus(FAGSAK_STATUS)
                .medAktørId(AKTØR_ID)
                .medAktørIdAnnenPart(AKTØR_ID_ANNEN_PART)
                .medFagsakYtelseType(FAGSAK_YTELSE_TYPE)
                .medFamilieHendelseType(FAMILIEHENDELSE_TYPE)
                .medSaksnummer(SAKSNUMMER)
                .medAktørIdBarn(AKTØR_ID)
                .build();

        SakStatusDto dto = SakStatusDto.fraDomene(sakStatus);
        assertThat(dto.getAktørId()).isEqualTo(AKTØR_ID);
        assertThat(dto.getAktørIdAnnenPart()).isEqualTo(AKTØR_ID_ANNEN_PART);
        assertThat(dto.getFagsakStatus()).isEqualTo(FAGSAK_STATUS);
        assertThat(dto.getBehandlingTema())
                .isEqualTo(BehandlingTema.fraYtelse(FAGSAK_YTELSE_TYPE, FAMILIEHENDELSE_TYPE));

        assertThat(dto.getLenker()).isEmpty();
        dto.leggTilLenke(TEST_LENKE, TEST_REL);
        assertThat(dto.getLenker().get(0)).isEqualTo(EXPECTED_RESOURCE_LINK);
    }

    @Test
    public void skalTransformereFullstendigMottattDokumentTilSøknadXmlDto() {
        MottattDokument dokument = lagDokument(BEHANDLING_ID, JOURNALPOST_ID, XML_PAYLOAD);

        SøknadXmlDto dto = SøknadXmlDto.fraDomene(dokument);
        assertThat(dto.getJournalpostId()).isEqualTo(JOURNALPOST_ID);
        assertThat(dto.getXml()).isEqualTo(XML_PAYLOAD);
    }

    @Test
    public void skalKasteFeilmeldingNårVedForsøkPåSammenslåingAvToUrelaterteDokumenter() {
        MottattDokument dokument1 = lagDokument(1L, JOURNALPOST_ID, XML_PAYLOAD);
        MottattDokument dokument2 = lagDokument(2L, JOURNALPOST_ID, XML_PAYLOAD);
        Assertions.assertThrows(TekniskException.class, () -> SøknadXmlDto.fraDomene(dokument1, dokument2));
    }

    @Test
    public void skalSammenslåToMottatteDokumentKnyttetTilSammeBehandlingTilSøknadXmlDto() {
        MottattDokument dokument1 = lagDokument(BEHANDLING_ID, JOURNALPOST_ID, null);
        MottattDokument dokument2 = lagDokument(BEHANDLING_ID, null, XML_PAYLOAD);

        SøknadXmlDto dto = SøknadXmlDto.fraDomene(dokument1, dokument2);

        assertThat(dto.getXml()).isEqualTo(XML_PAYLOAD);
        assertThat(dto.getJournalpostId()).isEqualTo(JOURNALPOST_ID);
    }

    @Test
    public void skalMappeUttakPeriodeTilUttakPeriodeDto() {
        var periodeResultatÅrsak = "4005";
        UttakPeriode uttakPeriode = UttakPeriode.builder()
                .withPeriodeResultatType("INNVILGET")
                .withSamtidigUttak(null)
                .withUttakUtsettelseType("-")
                .withGraderingAvslagAarsak(GraderingAvslagÅrsak.MOR_OPPFYLLER_IKKE_AKTIVITETSKRAV)
                .withPeriodeResultatÅrsak(periodeResultatÅrsak)
                .withBehandlingId(42L)
                .withUttakUtsettelseType("-")
                .withFlerbarnsdager(null)
                .withGraderingInnvilget(false)
                .withManueltBehandlet(true)
                .withFom(LocalDate.now().minusDays(30))
                .withTom(LocalDate.now().plusDays(20))
                .withTrekkkonto("MØDREKVOTE")
                .withTrekkdager(BigDecimal.valueOf(50L))
                .withArbeidstidprosent(0L)
                .withUtbetalingsprosent(100L)
                .withSamtidigUttaksprosent(null)
                .withOppholdÅrsak("-")
                .withOverføringÅrsak("-")
                .withMorsAktivitet(MorsAktivitet.UKJENT)
                .withUttakArbeidType("ORDINÆRT_ARBEID")
                .withArbeidsgiverAktørId(null)
                .withArbeidsgiverAktørId("11111111")
                .build();
        UttaksPeriodeDto uttaksPeriodeDto = UttaksPeriodeDto.fraDomene(new Saksnummer("1"), uttakPeriode, false);
        System.out.println(uttaksPeriodeDto);

        assertThat(uttaksPeriodeDto.getMorsAktivitet()).isEqualTo(null);
        assertThat(uttaksPeriodeDto.getGraderingAvslagAarsak()).isEqualTo("MOR_OPPFYLLER_IKKE_AKTIVITETSKRAV");
        assertThat(uttaksPeriodeDto.getPeriodeResultatÅrsak()).isEqualTo(periodeResultatÅrsak);
    }

    private MottattDokument lagDokument(Long behandlingId, String journalpostId, String xmlPayload) {
        return MottattDokument.builder()
                .medBehandlingId(behandlingId)
                .medMottattDokumentId(DOKUMENT_ID)
                .medBehandlingStatus(BEHANDLING_STATUS)
                .medForsendelseId(FORSENDELSE_ID)
                .medJournalpostId(journalpostId)
                .medSøknadXml(xmlPayload)
                .medType(DOKUMENT_TYPE_ID)
                .build();
    }
}
