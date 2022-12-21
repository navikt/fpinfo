package no.nav.foreldrepenger.info.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.UttakPeriode;
import no.nav.foreldrepenger.info.app.ResourceLink;
import no.nav.foreldrepenger.info.app.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.SakDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.UttaksPeriodeDto;
import no.nav.foreldrepenger.info.datatyper.BehandlingStatus;
import no.nav.foreldrepenger.info.datatyper.BehandlingTema;
import no.nav.foreldrepenger.info.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.datatyper.FamilieHendelseType;
import no.nav.foreldrepenger.info.datatyper.GraderingAvslagÅrsak;

class DomeneTilDtoTransformasjonTest {

    private static final Long BEHANDLING_ID = 1234L;
    private static final Saksnummer SAKSNUMMER = new Saksnummer("4567");
    private static final String BEHANDLENDE_ENHET = "4052";
    private static final String BEHANDLENDE_ENHET_NAVN = "NAV enhet";
    private static final String BEHANDLING_STATUS = BehandlingStatus.UTREDES.getVerdi();
    private static final String FAGSAK_YTELSE_TYPE = FagsakYtelseType.FP.name();
    private static final String FAMILIEHENDELSE_TYPE = FamilieHendelseType.FØDSEL.getVerdi();
    private static final String TEST_LENKE = "test-lenke";
    private static final String TEST_REL = "test-rel";
    private static final String FAGSAK_STATUS = "OPPR";
    private static final String AKTØR_ID = "1234";
    private static final String AKTØR_ID_ANNEN_PART = "5678";
    private static final ResourceLink EXPECTED_RESOURCE_LINK = ResourceLink.get(TEST_LENKE, TEST_REL, null);

    @Test
    void behandlingTilBehandlingDto() {
        var behandling = Behandling.builder()
                .medBehandlingId(BEHANDLING_ID)
                .medBehandlingResultatType("IKKE_FASTSATT")
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
}
