package no.nav.foreldrepenger.info.v2;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.info.v2.persondetaljer.AktørId;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.FagsakRelasjon;
import no.nav.foreldrepenger.info.InMemTestRepository;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.SøknadsGrunnlagRettigheter;
import no.nav.foreldrepenger.info.UttakPeriode;
import no.nav.foreldrepenger.info.datatyper.BehandlingType;
import no.nav.foreldrepenger.info.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.datatyper.FamilieHendelseType;

class SakRestTest {

    @Test
    void henter_fp_sak_med_vedtak() {
        var repository = new InMemTestRepository();
        var saksnummer = new no.nav.foreldrepenger.info.Saksnummer("123");
        var barnAktørId = "barnId";
        var annenPartAktørId = "annenpart";
        var behandlingId = 345L;
        var aktørId = new SakRest.AktørIdDto("000000000");
        var fødselsdato = LocalDate.now();
        var termindato = LocalDate.now().minusDays(2);

        repository.lagre(new Sak.Builder()
                        .medSaksnummer(saksnummer)
                        .medBehandlingId(String.valueOf(behandlingId))
                        .medFagsakStatus("OPPR")
                        .medAktørId(aktørId.aktørId())
                        .medFamilieHendelseType(FamilieHendelseType.FØDSEL.getVerdi())
                        .medFagsakYtelseType(FagsakYtelseType.FP.name())
                        .medAktørIdBarn(barnAktørId)
                        .medAktørIdAnnenPart(annenPartAktørId)
                .build());
        var annenpartSaksnummer = "111";
        repository.lagre(new FagsakRelasjon.Builder()
                        .saksnummerEn(saksnummer.saksnummer())
                        .saksnummerTo(annenpartSaksnummer)
                .build());
        var behandling = new Behandling.Builder()
                .medBehandlingId(behandlingId)
                .medBehandlingStatus("AVSLU")
                .medFamilieHendelseType(FamilieHendelseType.FØDSEL.getVerdi())
                .medSaksnummer(saksnummer)
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .build();
        repository.lagre(behandling);
        var uttakPeriode = new UttakPeriode.Builder()
                .withFom(LocalDate.now())
                .withTom(LocalDate.now().plusWeeks(10))
                .withArbeidstidprosent(30L)
                .withGraderingInnvilget(true)
                .withUttakUtsettelseType("-")
                .withPeriodeResultatType("INNVILGET")
                .withPeriodeResultatÅrsak(InnvilgetÅrsak.KVOTE_ELLER_OVERFØRT_KVOTE.kode())
                .withTrekkkonto(KontoType.MØDREKVOTE.name())
                .withBehandlingId(behandlingId)
                .withFlerbarnsdager(false)
                .withOppholdÅrsak("UTTAK_MØDREKVOTE_ANNEN_FORELDER")
                .withOverføringÅrsak("SYKDOM_ANNEN_FORELDER")
                .withUttakUtsettelseType("INSTITUSJONSOPPHOLD_SØKER")
                .build();
        repository.lagre(behandlingId, List.of(uttakPeriode));

        var søknadsGrunnlag = new SøknadsGrunnlag.Builder()
                .antallBarn(1)
                .annenForelderInformert(true)
                .familieHendelseType(behandling.getFamilieHendelseType())
                .brukerRolle("MORA")
                .dekningsgrad(80)
                .behandlingId(behandlingId)
                .fødselDato(fødselsdato)
                .termindato(termindato)
                .foreldreRettigheter(new SøknadsGrunnlagRettigheter(1L, null, true, null, false))
                .build();
        repository.lagre(behandlingId, søknadsGrunnlag);


        var sakerTjeneste = new SakerTjeneste(repository);
        var sakRest = new SakRest(sakerTjeneste, new AnnenPartsVedtaksperioderTjeneste(sakerTjeneste));
        var saker = sakRest.hentSaker(aktørId);

        assertThat(saker.foreldrepenger()).hasSize(1);

        var fpSak = saker.foreldrepenger().stream().findFirst().orElseThrow();
        assertThat(fpSak.saksnummer().value()).isEqualTo(saksnummer.saksnummer());
        assertThat(fpSak.annenPart() .personDetaljer()).isEqualTo(new AktørId(annenPartAktørId));
        assertThat(fpSak.barn()).hasSize(1);
        assertThat(fpSak.barn().stream().findFirst().orElseThrow()).isEqualTo(new AktørId(barnAktørId));
        assertThat(fpSak.sakAvsluttet()).isFalse();
        assertThat(fpSak.kanSøkeOmEndring()).isTrue();
        assertThat(fpSak.sakTilhørerMor()).isTrue();
        assertThat(fpSak.familiehendelse().fødselsdato()).isEqualTo(fødselsdato);
        assertThat(fpSak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(fpSak.gjelderAdopsjon()).isFalse();
        assertThat(fpSak.gjeldendeVedtak()).isNotNull();
        assertThat(fpSak.gjeldendeVedtak().perioder()).hasSize(1);
        assertThat(fpSak.dekningsgrad()).isEqualTo(Dekningsgrad.ÅTTI);
        var vedtakPeriode = fpSak.gjeldendeVedtak().perioder().get(0);
        assertThat(vedtakPeriode.flerbarnsdager()).isEqualTo(uttakPeriode.getFlerbarnsdager());
        assertThat(vedtakPeriode.fom()).isEqualTo(uttakPeriode.getFom());
        assertThat(vedtakPeriode.tom()).isEqualTo(uttakPeriode.getTom());
        assertThat(vedtakPeriode.kontoType()).isEqualTo(KontoType.valueOf(uttakPeriode.getTrekkonto()));
        assertThat(vedtakPeriode.resultat().innvilget()).isTrue();
        //assertThat(vedtakPeriode.resultat().årsak()).isEqualTo(InnvilgetÅrsak.fraKode(uttakPeriode.getPeriodeResultatÅrsak())); //TODO: fjernet dette feltet
        assertThat(vedtakPeriode.gradering()).isEqualTo(new Gradering(
                BigDecimal.valueOf(uttakPeriode.getArbeidstidprosent())));
        assertThat(vedtakPeriode.morsAktivitet()).isNull();
        assertThat(vedtakPeriode.samtidigUttak()).isNull();
        assertThat(vedtakPeriode.flerbarnsdager()).isEqualTo(uttakPeriode.getFlerbarnsdager());

        //I praksis kan ikke alle disse være satt samtidig
        assertThat(vedtakPeriode.utsettelseÅrsak()).isEqualTo(UtsettelseÅrsak.SØKER_INNLAGT);
        assertThat(vedtakPeriode.overføringÅrsak()).isEqualTo(OverføringÅrsak.SYKDOM_ANNEN_FORELDER);
        assertThat(vedtakPeriode.oppholdÅrsak()).isEqualTo(OppholdÅrsak.MØDREKVOTE_ANNEN_FORELDER);

    }

    @Test
    void henter_fp_sak_uten_vedtak() {
        var repository = new InMemTestRepository();
        var saksnummer = new no.nav.foreldrepenger.info.Saksnummer("123");
        var barnAktørId = "barnId";
        var annenPartAktørId = "annenpart";
        var behandlingId = 345L;
        var aktørId = new SakRest.AktørIdDto("000000000");
        var fødselsdato = LocalDate.now();

        repository.lagre(new Sak.Builder()
                .medSaksnummer(saksnummer)
                .medBehandlingId(String.valueOf(behandlingId))
                .medFagsakStatus("OPPR")
                .medAktørId(aktørId.aktørId())
                .medFamilieHendelseType(FamilieHendelseType.FØDSEL.getVerdi())
                .medFagsakYtelseType(FagsakYtelseType.FP.name())
                .medAktørIdBarn(barnAktørId)
                .medAktørIdAnnenPart(annenPartAktørId)
                .build());
        var annenpartSaksnummer = "111";
        repository.lagre(new FagsakRelasjon.Builder()
                .saksnummerEn(saksnummer.saksnummer())
                .saksnummerTo(annenpartSaksnummer)
                .build());
        var behandling = new Behandling.Builder()
                .medBehandlingId(behandlingId)
                .medBehandlingStatus("OPPR")
                .medFamilieHendelseType(FamilieHendelseType.FØDSEL.getVerdi())
                .medSaksnummer(saksnummer)
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .build();
        repository.lagre(behandling);

        var søknadsGrunnlag = new SøknadsGrunnlag.Builder()
                .antallBarn(1)
                .annenForelderInformert(true)
                .familieHendelseType(behandling.getFamilieHendelseType())
                .brukerRolle("FARA")
                .dekningsgrad(100)
                .behandlingId(behandlingId)
                .fødselDato(fødselsdato)
                .foreldreRettigheter(new SøknadsGrunnlagRettigheter(1L, null, true, null, false))
                .build();
        repository.lagre(behandlingId, søknadsGrunnlag);

        //Lagrer dokument for at behandlingen skal være relevant
        var mottattDokument = new MottattDokument.Builder()
                .medBehandlingId(behandlingId)
                .medType(DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL)
                .build();
        repository.lagre(List.of(mottattDokument));

        var sakerTjeneste = new SakerTjeneste(repository);
        var sakRest = new SakRest(sakerTjeneste, new AnnenPartsVedtaksperioderTjeneste(sakerTjeneste));
        var saker = sakRest.hentSaker(aktørId);

        assertThat(saker.foreldrepenger()).hasSize(1);

        var fpSak = saker.foreldrepenger().stream().findFirst().orElseThrow();
        assertThat(fpSak.saksnummer().value()).isEqualTo(saksnummer.saksnummer());
        assertThat(fpSak.annenPart().personDetaljer()).isEqualTo(new AktørId(annenPartAktørId));
        assertThat(fpSak.barn()).hasSize(1);
        assertThat(fpSak.barn().stream().findFirst().orElseThrow()).isEqualTo(new AktørId(barnAktørId));
        assertThat(fpSak.sakAvsluttet()).isFalse();
        assertThat(fpSak.kanSøkeOmEndring()).isFalse();
        assertThat(fpSak.sakTilhørerMor()).isFalse();
        assertThat(fpSak.familiehendelse().fødselsdato()).isEqualTo(fødselsdato);
        assertThat(fpSak.gjelderAdopsjon()).isFalse();
        assertThat(fpSak.gjeldendeVedtak()).isNull();
        assertThat(fpSak.dekningsgrad()).isEqualTo(Dekningsgrad.HUNDRE);
    }

}
