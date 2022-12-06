package no.nav.foreldrepenger.info.v2;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.common.innsyn.v2.Aktivitet;
import no.nav.foreldrepenger.common.innsyn.v2.Arbeidsgiver;
import no.nav.foreldrepenger.common.innsyn.v2.OppholdÅrsak;
import no.nav.foreldrepenger.common.innsyn.v2.UtsettelseÅrsak;
import no.nav.foreldrepenger.common.innsyn.v2.UttakPeriodeResultat;
import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.FagsakRelasjon;
import no.nav.foreldrepenger.info.InMemTestRepository;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.SøknadsGrunnlagRettigheter;
import no.nav.foreldrepenger.info.SøknadsperiodeEntitet;
import no.nav.foreldrepenger.info.UføreGrunnlag;
import no.nav.foreldrepenger.info.UttakPeriode;
import no.nav.foreldrepenger.info.datatyper.BehandlingType;
import no.nav.foreldrepenger.info.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.datatyper.FamilieHendelseType;
import no.nav.foreldrepenger.info.datatyper.MorsAktivitet;

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
                .withUttakArbeidType(Aktivitet.Type.ORDINÆRT_ARBEID.name())
                .withArbeidsgiverOrgnr("123")
                .withPeriodeResultatType("INNVILGET")
                .withPeriodeResultatÅrsak("2003")
                .withTrekkkonto(KontoType.MØDREKVOTE.name())
                .withBehandlingId(behandlingId)
                .withFlerbarnsdager(false)
                .withOppholdÅrsak("UTTAK_MØDREKVOTE_ANNEN_FORELDER")
                .withOverføringÅrsak("SYKDOM_ANNEN_FORELDER")
                .withUttakUtsettelseType("INSTITUSJONSOPPHOLD_SØKER")
                .withTrekkdager(BigDecimal.TEN)
                .build();
        var avslagHull = new UttakPeriode.Builder()
                .withFom(uttakPeriode.getTom().plusDays(1))
                .withTom(uttakPeriode.getTom().plusWeeks(1))
                .withArbeidstidprosent(0L)
                .withUttakUtsettelseType("-")
                .withUttakArbeidType(Aktivitet.Type.ORDINÆRT_ARBEID.name())
                .withArbeidsgiverOrgnr("123")
                .withPeriodeResultatType("IKKE_OPPFYLT")
                .withPeriodeResultatÅrsak("4005")
                .withTrekkkonto(KontoType.FELLESPERIODE.name())
                .withBehandlingId(behandlingId)
                .withTrekkdager(BigDecimal.TEN)
                .build();
        repository.lagreVedtaksperioder(behandlingId, List.of(uttakPeriode, avslagHull));

        var søknadsGrunnlag = new SøknadsGrunnlag.Builder()
                .antallBarn(1)
                .annenForelderInformert(true)
                .familieHendelseType(behandling.getFamilieHendelseType())
                .brukerRolle("MORA")
                .dekningsgrad(80)
                .behandlingId(behandlingId)
                .fødselDato(fødselsdato)
                .termindato(termindato)
                .foreldreRettigheter(new SøknadsGrunnlagRettigheter(1L, null, true, null, false, true, true))
                .uføreGrunnlag(new UføreGrunnlag(behandlingId, true, false))
                .build();
        repository.lagre(behandlingId, søknadsGrunnlag);

        var søknadMottattDato = LocalDate.now().minusWeeks(1);
        lagreSøknad(repository, behandlingId, søknadMottattDato);

        var sakerTjeneste = new SakerTjeneste(repository);
        var sakRest = new SakRest(sakerTjeneste, new AnnenPartVedtakTjeneste(sakerTjeneste));
        var saker = sakRest.hentSaker(aktørId);

        assertThat(saker.foreldrepenger()).hasSize(1);

        var fpSak = saker.foreldrepenger().stream().findFirst().orElseThrow();
        assertThat(fpSak.saksnummer().value()).isEqualTo(saksnummer.saksnummer());
        assertThat(fpSak.annenPart().personDetaljer()).isEqualTo(new no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.AktørId(annenPartAktørId));
        assertThat(fpSak.barn()).hasSize(1);
        assertThat(fpSak.barn().stream().findFirst().orElseThrow()).isEqualTo(new no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.AktørId(barnAktørId));
        assertThat(fpSak.sakAvsluttet()).isFalse();
        assertThat(fpSak.kanSøkeOmEndring()).isTrue();
        assertThat(fpSak.sakTilhørerMor()).isTrue();
        assertThat(fpSak.morUføretrygd()).isFalse();
        assertThat(fpSak.harAnnenForelderTilsvarendeRettEØS()).isTrue();
        assertThat(fpSak.familiehendelse().fødselsdato()).isEqualTo(fødselsdato);
        assertThat(fpSak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(fpSak.gjelderAdopsjon()).isFalse();
        assertThat(fpSak.gjeldendeVedtak()).isNotNull();
        assertThat(fpSak.sisteSøknadMottattDato()).isEqualTo(søknadMottattDato);
        assertThat(fpSak.gjeldendeVedtak().perioder()).hasSize(2);
        assertThat(fpSak.dekningsgrad()).isEqualTo(no.nav.foreldrepenger.common.innsyn.v2.Dekningsgrad.ÅTTI);
        var vedtakPeriode = fpSak.gjeldendeVedtak().perioder().get(0);
        assertThat(vedtakPeriode.fom()).isEqualTo(uttakPeriode.getFom());
        assertThat(vedtakPeriode.tom()).isEqualTo(uttakPeriode.getTom());
        assertThat(vedtakPeriode.kontoType()).isEqualTo(no.nav.foreldrepenger.common.innsyn.v2.KontoType.valueOf(uttakPeriode.getTrekkonto()));
        assertThat(vedtakPeriode.resultat().innvilget()).isTrue();
        assertThat(vedtakPeriode.gradering()).isEqualTo(new no.nav.foreldrepenger.common.innsyn.v2.Gradering(
                BigDecimal.valueOf(uttakPeriode.getArbeidstidprosent()), new Aktivitet(Aktivitet.Type.ORDINÆRT_ARBEID,
                new Arbeidsgiver(uttakPeriode.getArbeidsgiverOrgnr(), Arbeidsgiver.ArbeidsgiverType.ORGANISASJON))));
        assertThat(vedtakPeriode.morsAktivitet()).isNull();
        assertThat(vedtakPeriode.samtidigUttak()).isNull();
        assertThat(vedtakPeriode.flerbarnsdager()).isEqualTo(uttakPeriode.getFlerbarnsdager());
        assertThat(vedtakPeriode.resultat().innvilget()).isTrue();
        assertThat(vedtakPeriode.resultat().trekkerDager()).isTrue();
        assertThat(vedtakPeriode.resultat().årsak()).isEqualTo(UttakPeriodeResultat.Årsak.ANNET);

        //I praksis kan ikke alle disse være satt samtidig
        assertThat(vedtakPeriode.utsettelseÅrsak()).isEqualTo(no.nav.foreldrepenger.common.innsyn.v2.UtsettelseÅrsak.SØKER_INNLAGT);
        assertThat(vedtakPeriode.overføringÅrsak()).isEqualTo(no.nav.foreldrepenger.common.innsyn.v2.OverføringÅrsak.SYKDOM_ANNEN_FORELDER);
        assertThat(vedtakPeriode.oppholdÅrsak()).isEqualTo(no.nav.foreldrepenger.common.innsyn.v2.OppholdÅrsak.MØDREKVOTE_ANNEN_FORELDER);

        var avslagsperiode = fpSak.gjeldendeVedtak().perioder().get(1);
        assertThat(avslagsperiode.fom()).isEqualTo(avslagHull.getFom());
        assertThat(avslagsperiode.tom()).isEqualTo(avslagHull.getTom());
        assertThat(avslagsperiode.resultat().innvilget()).isFalse();
        assertThat(avslagsperiode.resultat().trekkerDager()).isTrue();
        assertThat(avslagsperiode.resultat().årsak()).isEqualTo(UttakPeriodeResultat.Årsak.AVSLAG_HULL_MELLOM_FORELDRENES_PERIODER);
    }

    private void lagreSøknad(InMemTestRepository repository, long behandlingId, LocalDate søknadMottattDato) {
        var mottattDokument = new MottattDokument.Builder()
                .medBehandlingId(behandlingId)
                .medType(DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL)
                .medMottattDato(søknadMottattDato)
                .build();
        repository.lagre(List.of(mottattDokument));
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

        var søknadsperiode = new SøknadsperiodeEntitet.Builder()
                .fom(LocalDate.now())
                .tom(LocalDate.now().plusWeeks(10).minusDays(1))
                .gradering(30L, "123", null, Aktivitet.Type.ORDINÆRT_ARBEID)
                .utsettelseÅrsak("INSTITUSJONSOPPHOLD_SØKER")
                .arbeidsgiverOrgnr("123")
                .trekkonto(KontoType.FELLESPERIODE.name())
                .flerbarnsdager(false)
                .morsAktivitet(MorsAktivitet.INNLAGT)
                .build();
        var opphold = new SøknadsperiodeEntitet.Builder()
                .fom(LocalDate.now().plusWeeks(10))
                .tom(LocalDate.now().plusWeeks(12).minusDays(1))
                .oppholdÅrsak("UTTAK_FEDREKVOTE_ANNEN_FORELDER")
                .trekkonto("ANNET")
                .build();
        repository.lagreSøknadsperioder(behandlingId, List.of(søknadsperiode, opphold));

        var søknadsGrunnlag = new SøknadsGrunnlag.Builder()
                .antallBarn(1)
                .annenForelderInformert(true)
                .familieHendelseType(behandling.getFamilieHendelseType())
                .brukerRolle("FARA")
                .dekningsgrad(100)
                .behandlingId(behandlingId)
                .fødselDato(fødselsdato)
                .foreldreRettigheter(new SøknadsGrunnlagRettigheter(1L, null, true, null, false, false, false))
                .build();
        repository.lagre(behandlingId, søknadsGrunnlag);

        //Lagrer dokument for at behandlingen skal være relevant
        var sisteSøknadMottattDato = LocalDate.now().minusWeeks(1);
        var førsteSøknadMottattDato = LocalDate.now().minusWeeks(4);
        lagreSøknad(repository, behandlingId, sisteSøknadMottattDato);
        lagreSøknad(repository, behandlingId, førsteSøknadMottattDato);

        var sakerTjeneste = new SakerTjeneste(repository);
        var sakRest = new SakRest(sakerTjeneste, new AnnenPartVedtakTjeneste(sakerTjeneste));
        var saker = sakRest.hentSaker(aktørId);

        assertThat(saker.foreldrepenger()).hasSize(1);

        var fpSak = saker.foreldrepenger().stream().findFirst().orElseThrow();
        assertThat(fpSak.saksnummer().value()).isEqualTo(saksnummer.saksnummer());
        assertThat(fpSak.annenPart().personDetaljer()).isEqualTo(new no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.AktørId(annenPartAktørId));
        assertThat(fpSak.barn()).hasSize(1);
        assertThat(fpSak.barn().stream().findFirst().orElseThrow()).isEqualTo(new no.nav.foreldrepenger.common.innsyn.v2.persondetaljer.AktørId(barnAktørId));
        assertThat(fpSak.sakAvsluttet()).isFalse();
        assertThat(fpSak.kanSøkeOmEndring()).isFalse();
        assertThat(fpSak.sakTilhørerMor()).isFalse();
        assertThat(fpSak.morUføretrygd()).isFalse();
        assertThat(fpSak.harAnnenForelderTilsvarendeRettEØS()).isFalse();
        assertThat(fpSak.familiehendelse().fødselsdato()).isEqualTo(fødselsdato);
        assertThat(fpSak.gjelderAdopsjon()).isFalse();
        assertThat(fpSak.gjeldendeVedtak()).isNull();
        assertThat(fpSak.sisteSøknadMottattDato()).isEqualTo(sisteSøknadMottattDato);
        assertThat(fpSak.dekningsgrad()).isEqualTo(no.nav.foreldrepenger.common.innsyn.v2.Dekningsgrad.HUNDRE);

        assertThat(fpSak.åpenBehandling().søknadsperioder()).hasSize(2);
        var søknadsperiode1 = fpSak.åpenBehandling().søknadsperioder().get(0);
        assertThat(søknadsperiode1.flerbarnsdager()).isEqualTo(søknadsperiode.isFlerbarnsdager());
        assertThat(søknadsperiode1.fom()).isEqualTo(søknadsperiode.getFom());
        assertThat(søknadsperiode1.tom()).isEqualTo(søknadsperiode.getTom());
        assertThat(søknadsperiode1.kontoType()).isEqualTo(no.nav.foreldrepenger.common.innsyn.v2.KontoType.valueOf(søknadsperiode.getTrekkonto().orElseThrow()));
        assertThat(søknadsperiode1.gradering()).isEqualTo(new no.nav.foreldrepenger.common.innsyn.v2.Gradering(
                BigDecimal.valueOf(søknadsperiode.getArbeidstidprosent()), new Aktivitet(Aktivitet.Type.ORDINÆRT_ARBEID,
                new Arbeidsgiver(søknadsperiode.getArbeidsgiverOrgnr(), Arbeidsgiver.ArbeidsgiverType.ORGANISASJON))));
        assertThat(søknadsperiode1.morsAktivitet().name()).isEqualTo(søknadsperiode.getMorsAktivitet().name());
        assertThat(søknadsperiode1.samtidigUttak()).isNull();

        //I praksis kan ikke alle disse være satt samtidig
        assertThat(søknadsperiode1.utsettelseÅrsak()).isEqualTo(UtsettelseÅrsak.SØKER_INNLAGT);
        assertThat(søknadsperiode1.overføringÅrsak()).isNull();
        assertThat(søknadsperiode1.oppholdÅrsak()).isNull();

        var oppholdsperiode = fpSak.åpenBehandling().søknadsperioder().get(1);
        assertThat(oppholdsperiode.fom()).isEqualTo(opphold.getFom());
        assertThat(oppholdsperiode.tom()).isEqualTo(opphold.getTom());
        assertThat(oppholdsperiode.oppholdÅrsak()).isEqualTo(OppholdÅrsak.FEDREKVOTE_ANNEN_FORELDER);
        assertThat(oppholdsperiode.kontoType()).isNull();
    }

    @Test
    void henter_adopsjon() {
        var repository = new InMemTestRepository();
        var saksnummer = new no.nav.foreldrepenger.info.Saksnummer("123");
        var barnAktørId = "barnId";
        var annenPartAktørId = "annenpart";
        var behandlingId = 345L;
        var aktørId = new SakRest.AktørIdDto("000000000");
        var omsorgsovertakelse = LocalDate.now();

        repository.lagre(new Sak.Builder()
                .medSaksnummer(saksnummer)
                .medBehandlingId(String.valueOf(behandlingId))
                .medFagsakStatus("OPPR")
                .medAktørId(aktørId.aktørId())
                .medFamilieHendelseType(FamilieHendelseType.ADOPSJON.getVerdi())
                .medFagsakYtelseType(FagsakYtelseType.FP.name())
                .medAktørIdBarn(barnAktørId)
                .medAktørIdAnnenPart(annenPartAktørId)
                .build());
        var behandling = new Behandling.Builder()
                .medBehandlingId(behandlingId)
                .medBehandlingStatus("AVSLU")
                .medFamilieHendelseType(FamilieHendelseType.ADOPSJON.getVerdi())
                .medSaksnummer(saksnummer)
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .build();
        repository.lagre(behandling);

        var søknadsGrunnlag = new SøknadsGrunnlag.Builder()
                .antallBarn(1)
                .annenForelderInformert(true)
                .familieHendelseType(behandling.getFamilieHendelseType())
                .brukerRolle("MORA")
                .dekningsgrad(100)
                .behandlingId(behandlingId)
                .omsorgsovertakelseDato(omsorgsovertakelse)
                .fødselDato(omsorgsovertakelse)
                .foreldreRettigheter(new SøknadsGrunnlagRettigheter(1L, null, true, null, false, true, true))
                .uføreGrunnlag(new UføreGrunnlag(behandlingId, false, false))
                .build();
        repository.lagre(behandlingId, søknadsGrunnlag);

        var sakerTjeneste = new SakerTjeneste(repository);
        var sakRest = new SakRest(sakerTjeneste, new AnnenPartVedtakTjeneste(sakerTjeneste));
        var saker = sakRest.hentSaker(aktørId);

        assertThat(saker.foreldrepenger()).hasSize(1);

        var fpSak = saker.foreldrepenger().stream().findFirst().orElseThrow();
        assertThat(fpSak.saksnummer().value()).isEqualTo(saksnummer.saksnummer());
        assertThat(fpSak.familiehendelse().omsorgsovertakelse()).isEqualTo(omsorgsovertakelse);
        assertThat(fpSak.familiehendelse().fødselsdato()).isEqualTo(omsorgsovertakelse);
        assertThat(fpSak.familiehendelse().termindato()).isNull();
        assertThat(fpSak.gjelderAdopsjon()).isTrue();
    }

}
