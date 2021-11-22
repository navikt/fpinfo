package no.nav.foreldrepenger.info.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.UttakPeriode;
import no.nav.foreldrepenger.info.app.ResourceLink;
import no.nav.foreldrepenger.info.app.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.SakDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.SøknadXmlDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.UttaksPeriodeDto;
import no.nav.foreldrepenger.info.datatyper.BehandlingResultatType;
import no.nav.foreldrepenger.info.datatyper.BehandlingStatus;
import no.nav.foreldrepenger.info.datatyper.BehandlingTema;
import no.nav.foreldrepenger.info.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.datatyper.FamilieHendelseType;
import no.nav.foreldrepenger.info.datatyper.GraderingAvslagÅrsak;
import no.nav.vedtak.exception.TekniskException;

class DomeneTilDtoTransformasjonTest {
    private static final Long BEHANDLING_ID = 1234L;
    private static final Saksnummer SAKSNUMMER = new Saksnummer("4567");
    private static final String BEHANDLENDE_ENHET = "4052";
    private static final String BEHANDLENDE_ENHET_NAVN = "NAV enhet";
    private static final String RESULTAT_TYPE = BehandlingResultatType.IKKE_FASTSATT.name();
    private static final String BEHANDLING_STATUS = BehandlingStatus.UTREDES.getVerdi();
    private static final String FAGSAK_YTELSE_TYPE = FagsakYtelseType.FP.name();
    private static final String FAMILIEHENDELSE_TYPE = FamilieHendelseType.FØDSEL.getVerdi();
    private static final String TEST_LENKE = "test-lenke";
    private static final String TEST_REL = "test-rel";
    private static final String FAGSAK_STATUS = "OPPR";
    private static final String AKTØR_ID = "1234";
    private static final String AKTØR_ID_ANNEN_PART = "5678";
    private static final String JOURNALPOST_ID = "123456789";
    private static final String XML_PAYLOAD = "xml";
    private static final ResourceLink EXPECTED_RESOURCE_LINK = ResourceLink.get(TEST_LENKE, TEST_REL, null);
    private static final String DOKUMENT_ID = "123";
    private static final UUID FORSENDELSE_ID = UUID.randomUUID();
    private static final DokumentTypeId DOKUMENT_TYPE_ID = DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL;

    @Test
    void behandlingTilBehandlingDto() {
        var behandling = Behandling.builder()
                .medBehandlingId(BEHANDLING_ID)
                .medBehandlingResultatType(RESULTAT_TYPE)
                .medBehandlingStatus(BEHANDLING_STATUS)
                .medBehandlendeEnhet(BEHANDLENDE_ENHET, BEHANDLENDE_ENHET_NAVN)
                .medFagsakYtelseType(FAGSAK_YTELSE_TYPE)
                .medFamilieHendelseType(FAMILIEHENDELSE_TYPE)
                .medSaksnummer(SAKSNUMMER)
                .build();

        String forventetBehandlingTema = BehandlingTema.fraYtelse(FAGSAK_YTELSE_TYPE, FAMILIEHENDELSE_TYPE);

        var dto = BehandlingDto.fraDomene(behandling);
        assertThat(dto.getStatus()).isEqualTo(BEHANDLING_STATUS);
        assertThat(dto.getTema()).isEqualTo(forventetBehandlingTema);
        assertThat(dto.getType()).isEqualTo(FAGSAK_YTELSE_TYPE);
        assertThat(dto.getBehandlendeEnhet()).isEqualTo(BEHANDLENDE_ENHET);
        assertThat(dto.getBehandlendeEnhetNavn()).isEqualTo(BEHANDLENDE_ENHET_NAVN);
        assertThat(dto.getLenker()).isEmpty();

        dto.leggTilLenke(TEST_LENKE, TEST_REL);

        assertThat(dto.getLenker()).hasSize(1);
        assertThat(dto.getLenker().get(0)).isEqualTo(EXPECTED_RESOURCE_LINK);
    }

    @Test
    void sakStatusTilSakStatusDto() {
        var sak = Sak.builder()
                .medFagsakStatus(FAGSAK_STATUS)
                .medAktørId(AKTØR_ID)
                .medAktørIdAnnenPart(AKTØR_ID_ANNEN_PART)
                .medFagsakYtelseType(FAGSAK_YTELSE_TYPE)
                .medFamilieHendelseType(FAMILIEHENDELSE_TYPE)
                .medSaksnummer(SAKSNUMMER)
                .medAktørIdBarn(AKTØR_ID)
                .build();

        var dto = SakDto.fraDomene(sak, true);
        assertThat(dto.getAktørId()).isEqualTo(AKTØR_ID);
        assertThat(dto.isMottattEndringssøknad()).isTrue();
        assertThat(dto.getAktørIdAnnenPart()).isEqualTo(AKTØR_ID_ANNEN_PART);
        assertThat(dto.getFagsakStatus()).isEqualTo(FAGSAK_STATUS);
        assertThat(dto.getBehandlingTema())
                .isEqualTo(BehandlingTema.fraYtelse(FAGSAK_YTELSE_TYPE, FAMILIEHENDELSE_TYPE));

        assertThat(dto.getLenker()).isEmpty();
        dto.leggTilLenke(TEST_LENKE, TEST_REL);
        assertThat(dto.getLenker().get(0)).isEqualTo(EXPECTED_RESOURCE_LINK);
    }

    @Test
    void sakStatusTilSakStatusDtoHåndtereNullFamiliehendelse() {
        var sak = Sak.builder()
                .medFagsakStatus(FAGSAK_STATUS)
                .medAktørId(AKTØR_ID)
                .medAktørIdAnnenPart(AKTØR_ID_ANNEN_PART)
                .medFagsakYtelseType(FAGSAK_YTELSE_TYPE)
                .medFamilieHendelseType(null)
                .medSaksnummer(SAKSNUMMER)
                .medAktørIdBarn(null)
                .build();

        var dto = SakDto.fraDomene(sak, true);
        assertThat(dto.getBehandlingTema()).isEqualTo(BehandlingTema.FORELDREPENGER);
        assertThat(dto.getAktørIdBarna()).isEmpty();
    }

    @Test
    void fullstendigMottattDokumentTilSøknadXmlDto() {
        var dokument = lagDokument(BEHANDLING_ID, JOURNALPOST_ID, XML_PAYLOAD);

        var dto = new SøknadXmlDto(dokument);
        assertThat(dto.journalpostId()).isEqualTo(JOURNALPOST_ID);
        assertThat(dto.xml()).isEqualTo(XML_PAYLOAD);
    }

    @Test
    void skalKasteFeilmeldingNårVedForsøkPåSammenslåingAvToUrelaterteDokumenter() {
        var dokument1 = lagDokument(1L, JOURNALPOST_ID, XML_PAYLOAD);
        var dokument2 = lagDokument(2L, JOURNALPOST_ID, XML_PAYLOAD);
        assertThrows(TekniskException.class, () -> SøknadXmlDto.fraDomene(dokument1, dokument2));
    }

    @Test
    void skalSammenslåToMottatteDokumentKnyttetTilSammeBehandlingTilSøknadXmlDto() {
        var dokument1 = lagDokument(BEHANDLING_ID, JOURNALPOST_ID, null);
        var dokument2 = lagDokument(BEHANDLING_ID, null, XML_PAYLOAD);

        var dto = SøknadXmlDto.fraDomene(dokument1, dokument2);

        assertThat(dto.xml()).isEqualTo(XML_PAYLOAD);
        assertThat(dto.journalpostId()).isEqualTo(JOURNALPOST_ID);
    }

    @Test
    void skalMappeUttakPeriodeTilUttakPeriodeDto() {
        var periodeResultatÅrsak = "4005";
        var uttakPeriode = UttakPeriode.builder()
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
                .withMorsAktivitet(null)
                .withUttakArbeidType("ORDINÆRT_ARBEID")
                .withArbeidsgiverAktørId(null)
                .withArbeidsgiverAktørId("11111111")
                .build();
        var uttaksPeriodeDto = UttaksPeriodeDto.fraDomene(new Saksnummer("1"), uttakPeriode, false);

        assertThat(uttaksPeriodeDto.getMorsAktivitet()).isNull();
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
