package no.nav.foreldrepenger.info.v2;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.FamilieHendelse;
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
import no.nav.foreldrepenger.info.datatyper.MorsAktivitet;

class FpSakerTjenesteTest {

    @Test
    void henter_fp_sak_med_vedtak() {
        var repository = new InMemTestRepository();
        var saksnummer = new no.nav.foreldrepenger.info.Saksnummer("123");
        var barnAktørId = "barnId";
        var annenPartAktørId = "annenpart";
        var behandlingId = 345L;
        var aktørId = new AktørId("000");
        var fødselsdato = LocalDate.now();
        var termindato = LocalDate.now().minusDays(2);

        repository.lagre(new Sak.Builder()
                .medSaksnummer(saksnummer)
                .medBehandlingId(String.valueOf(behandlingId))
                .medFagsakStatus("OPPR")
                .medAktørId(aktørId.value())
                .medFagsakYtelseType(FagsakYtelseType.FP.name())
                .medAktørIdBarn(barnAktørId)
                .medAktørIdAnnenPart(annenPartAktørId)
                .build());
        var behandling = new Behandling.Builder()
                .medBehandlingId(behandlingId)
                .medBehandlingStatus("AVSLU")
                .medSaksnummer(saksnummer)
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .build();
        repository.lagre(behandling);
        var uttakPeriode = new UttakPeriode.Builder()
                .fom(LocalDate.now())
                .tom(LocalDate.now().plusWeeks(10))
                .arbeidstidprosent(BigDecimal.valueOf(30))
                .graderingInnvilget(true)
                .uttakArbeidType(Aktivitet.Type.ORDINÆRT_ARBEID.name())
                .arbeidsgiverOrgnr("123")
                .periodeResultatType("INNVILGET")
                .periodeResultatÅrsak("2003")
                .trekkkonto(KontoType.MØDREKVOTE.name())
                .behandlingId(behandlingId)
                .flerbarnsdager(false)
                .oppholdÅrsak("UTTAK_MØDREKVOTE_ANNEN_FORELDER")
                .overføringÅrsak("SYKDOM_ANNEN_FORELDER")
                .uttakUtsettelseType("INSTITUSJONSOPPHOLD_SØKER")
                .trekkdager(BigDecimal.TEN)
                .build();
        var avslagHull = new UttakPeriode.Builder()
                .fom(uttakPeriode.getTom().plusDays(1))
                .tom(uttakPeriode.getTom().plusWeeks(1))
                .arbeidstidprosent(BigDecimal.ZERO)
                .uttakUtsettelseType("-")
                .uttakArbeidType(Aktivitet.Type.ORDINÆRT_ARBEID.name())
                .arbeidsgiverOrgnr("123")
                .periodeResultatType("IKKE_OPPFYLT")
                .periodeResultatÅrsak("4005")
                .trekkkonto(KontoType.FELLESPERIODE.name())
                .behandlingId(behandlingId)
                .trekkdager(BigDecimal.TEN)
                .build();
        repository.lagreVedtaksperioder(behandlingId, List.of(uttakPeriode, avslagHull));

        var søknadsGrunnlag = new SøknadsGrunnlag.Builder()
                .brukerRolle("MORA")
                .dekningsgrad(80)
                .behandlingId(behandlingId)
                .foreldreRettigheter(new SøknadsGrunnlagRettigheter(1L, null, true, null, false, false, true, true, true))
                .uføreGrunnlag(new UføreGrunnlag(behandlingId, true))
                .build();
        repository.lagre(behandlingId, søknadsGrunnlag);
        repository.lagre(new FamilieHendelse(behandlingId, 1, termindato, fødselsdato, null));

        var søknadMottattDato = LocalDate.now().minusWeeks(1);
        lagreSøknad(repository, behandlingId, søknadMottattDato);

        var tjeneste = tjeneste(repository);
        var saker = tjeneste.hentFor(aktørId);

        assertThat(saker).hasSize(1);

        var fpSak = saker.stream().findFirst().orElseThrow();
        assertThat(fpSak.saksnummer().value()).isEqualTo(saksnummer.saksnummer());
        assertThat(fpSak.annenPart().aktørId().value()).isEqualTo(annenPartAktørId);
        assertThat(fpSak.barn()).hasSize(1);
        assertThat(fpSak.barn().stream().findFirst().orElseThrow().value()).isEqualTo(barnAktørId);
        assertThat(fpSak.sakAvsluttet()).isFalse();
        assertThat(fpSak.kanSøkeOmEndring()).isTrue();
        assertThat(fpSak.sakTilhørerMor()).isTrue();
        assertThat(fpSak.morUføretrygd()).isFalse();
        assertThat(fpSak.harAnnenForelderTilsvarendeRettEØS()).isTrue();
        assertThat(fpSak.familiehendelse().fødselsdato()).isEqualTo(fødselsdato);
        assertThat(fpSak.familiehendelse().termindato()).isEqualTo(termindato);
        assertThat(fpSak.gjeldendeVedtak()).isNotNull();
        assertThat(fpSak.sisteSøknadMottattDato()).isEqualTo(søknadMottattDato);
        assertThat(fpSak.gjeldendeVedtak().perioder()).hasSize(2);
        assertThat(fpSak.dekningsgrad()).isEqualTo(Dekningsgrad.ÅTTI);
        var vedtakPeriode = fpSak.gjeldendeVedtak().perioder().get(0);
        assertThat(vedtakPeriode.fom()).isEqualTo(uttakPeriode.getFom());
        assertThat(vedtakPeriode.tom()).isEqualTo(uttakPeriode.getTom());
        assertThat(vedtakPeriode.kontoType()).isEqualTo(KontoType.valueOf(uttakPeriode.getTrekkonto()));
        assertThat(vedtakPeriode.resultat().innvilget()).isTrue();
        assertThat(vedtakPeriode.gradering()).isEqualTo(new Gradering(uttakPeriode.getArbeidstidprosent(), new Aktivitet(Aktivitet.Type.ORDINÆRT_ARBEID,
                new Arbeidsgiver(uttakPeriode.getArbeidsgiverOrgnr(), Arbeidsgiver.ArbeidsgiverType.ORGANISASJON))));
        assertThat(vedtakPeriode.morsAktivitet()).isNull();
        assertThat(vedtakPeriode.samtidigUttak()).isNull();
        assertThat(vedtakPeriode.flerbarnsdager()).isEqualTo(uttakPeriode.getFlerbarnsdager());
        assertThat(vedtakPeriode.resultat().innvilget()).isTrue();
        assertThat(vedtakPeriode.resultat().trekkerDager()).isTrue();
        assertThat(vedtakPeriode.resultat().årsak()).isEqualTo(UttakPeriodeResultat.Årsak.ANNET);

        //I praksis kan ikke alle disse være satt samtidig
        assertThat(vedtakPeriode.utsettelseÅrsak()).isEqualTo(UtsettelseÅrsak.SØKER_INNLAGT);
        assertThat(vedtakPeriode.overføringÅrsak()).isEqualTo(OverføringÅrsak.SYKDOM_ANNEN_FORELDER);
        assertThat(vedtakPeriode.oppholdÅrsak()).isEqualTo(OppholdÅrsak.MØDREKVOTE_ANNEN_FORELDER);

        var avslagsperiode = fpSak.gjeldendeVedtak().perioder().get(1);
        assertThat(avslagsperiode.fom()).isEqualTo(avslagHull.getFom());
        assertThat(avslagsperiode.tom()).isEqualTo(avslagHull.getTom());
        assertThat(avslagsperiode.resultat().innvilget()).isFalse();
        assertThat(avslagsperiode.resultat().trekkerDager()).isTrue();
        assertThat(avslagsperiode.resultat().årsak()).isEqualTo(UttakPeriodeResultat.Årsak.AVSLAG_HULL_MELLOM_FORELDRENES_PERIODER);
    }

    private static FpSakerTjeneste tjeneste(InMemTestRepository repository) {
        return new FpSakerTjeneste(repository);
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
        var aktørId = new AktørId("000");
        var fødselsdato = LocalDate.now();

        repository.lagre(new Sak.Builder()
                .medSaksnummer(saksnummer)
                .medBehandlingId(String.valueOf(behandlingId))
                .medFagsakStatus("OPPR")
                .medAktørId(aktørId.value())
                .medFagsakYtelseType(FagsakYtelseType.FP.name())
                .medAktørIdBarn(barnAktørId)
                .medAktørIdAnnenPart(annenPartAktørId)
                .build());
        var behandling = new Behandling.Builder()
                .medBehandlingId(behandlingId)
                .medBehandlingStatus("OPPR")
                .medSaksnummer(saksnummer)
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .build();
        repository.lagre(behandling);

        var søknadsperiode = new SøknadsperiodeEntitet.Builder()
                .fom(LocalDate.now())
                .tom(LocalDate.now().plusWeeks(10).minusDays(1))
                .gradering(BigDecimal.valueOf(30), "123", null, no.nav.foreldrepenger.common.innsyn.Aktivitet.Type.ORDINÆRT_ARBEID)
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
                .brukerRolle("FARA")
                .dekningsgrad(100)
                .behandlingId(behandlingId)
                .foreldreRettigheter(new SøknadsGrunnlagRettigheter(1L, null, true, null, false, false, false, false, false))
                .build();
        repository.lagre(behandlingId, søknadsGrunnlag);
        repository.lagre(new FamilieHendelse(behandlingId, 1, null, fødselsdato, null));

        //Lagrer dokument for at behandlingen skal være relevant
        var sisteSøknadMottattDato = LocalDate.now().minusWeeks(1);
        var førsteSøknadMottattDato = LocalDate.now().minusWeeks(4);
        lagreSøknad(repository, behandlingId, sisteSøknadMottattDato);
        lagreSøknad(repository, behandlingId, førsteSøknadMottattDato);

        var tjeneste = tjeneste(repository);
        var saker = tjeneste.hentFor(aktørId);

        assertThat(saker).hasSize(1);

        var fpSak = saker.stream().findFirst().orElseThrow();
        assertThat(fpSak.saksnummer().value()).isEqualTo(saksnummer.saksnummer());
        assertThat(fpSak.annenPart().aktørId().value()).isEqualTo(annenPartAktørId);
        assertThat(fpSak.barn()).hasSize(1);
        assertThat(fpSak.barn().stream().findFirst().orElseThrow().value()).isEqualTo(barnAktørId);
        assertThat(fpSak.sakAvsluttet()).isFalse();
        assertThat(fpSak.kanSøkeOmEndring()).isFalse();
        assertThat(fpSak.sakTilhørerMor()).isFalse();
        assertThat(fpSak.morUføretrygd()).isFalse();
        assertThat(fpSak.harAnnenForelderTilsvarendeRettEØS()).isFalse();
        assertThat(fpSak.familiehendelse().fødselsdato()).isEqualTo(fødselsdato);
        assertThat(fpSak.gjeldendeVedtak()).isNull();
        assertThat(fpSak.sisteSøknadMottattDato()).isEqualTo(sisteSøknadMottattDato);
        assertThat(fpSak.dekningsgrad()).isEqualTo(Dekningsgrad.HUNDRE);

        assertThat(fpSak.åpenBehandling().søknadsperioder()).hasSize(2);
        var søknadsperiode1 = fpSak.åpenBehandling().søknadsperioder().get(0);
        assertThat(søknadsperiode1.flerbarnsdager()).isEqualTo(søknadsperiode.isFlerbarnsdager());
        assertThat(søknadsperiode1.fom()).isEqualTo(søknadsperiode.getFom());
        assertThat(søknadsperiode1.tom()).isEqualTo(søknadsperiode.getTom());
        assertThat(søknadsperiode1.kontoType()).isEqualTo(KontoType.valueOf(søknadsperiode.getTrekkonto().orElseThrow()));
        assertThat(søknadsperiode1.gradering()).isEqualTo(new Gradering(
                søknadsperiode.getArbeidstidprosent(), new Aktivitet(Aktivitet.Type.ORDINÆRT_ARBEID,
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
        var aktørId = new AktørId("000");
        var omsorgsovertakelse = LocalDate.now();

        repository.lagre(new Sak.Builder()
                .medSaksnummer(saksnummer)
                .medBehandlingId(String.valueOf(behandlingId))
                .medFagsakStatus("OPPR")
                .medAktørId(aktørId.value())
                .medFagsakYtelseType(FagsakYtelseType.FP.name())
                .medAktørIdBarn(barnAktørId)
                .medAktørIdAnnenPart(annenPartAktørId)
                .build());
        var behandling = new Behandling.Builder()
                .medBehandlingId(behandlingId)
                .medBehandlingStatus("AVSLU")
                .medSaksnummer(saksnummer)
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .build();
        repository.lagre(behandling);

        var søknadsGrunnlag = new SøknadsGrunnlag.Builder()
                .brukerRolle("MORA")
                .dekningsgrad(100)
                .behandlingId(behandlingId)
                .foreldreRettigheter(new SøknadsGrunnlagRettigheter(1L, null, true, null, false, false, true, true, true))
                .uføreGrunnlag(new UføreGrunnlag(behandlingId, false))
                .build();
        repository.lagre(behandlingId, søknadsGrunnlag);
        repository.lagre(new FamilieHendelse(behandlingId, 1, null, omsorgsovertakelse, omsorgsovertakelse));

        var tjeneste = tjeneste(repository);
        var saker = tjeneste.hentFor(aktørId);

        assertThat(saker).hasSize(1);

        var fpSak = saker.stream().findFirst().orElseThrow();
        assertThat(fpSak.saksnummer().value()).isEqualTo(saksnummer.saksnummer());
        assertThat(fpSak.familiehendelse().omsorgsovertakelse()).isEqualTo(omsorgsovertakelse);
        assertThat(fpSak.familiehendelse().fødselsdato()).isEqualTo(omsorgsovertakelse);
        assertThat(fpSak.familiehendelse().termindato()).isNull();
    }

    @Test
    void ikke_hente_dobbelt_opp_hvis_klage() {
        var repository = new InMemTestRepository();
        var saksnummer = new no.nav.foreldrepenger.info.Saksnummer("123");
        var barnAktørId = "barnId";
        var annenPartAktørId = "annenpart";
        var behandlingId1 = 345L;
        var aktørId = new AktørId("000");
        var omsorgsovertakelse = LocalDate.now();

        repository.lagre(new Sak.Builder()
                .medSaksnummer(saksnummer)
                .medBehandlingId(String.valueOf(behandlingId1))
                .medFagsakStatus("OPPR")
                .medAktørId(aktørId.value())
                .medFagsakYtelseType(FagsakYtelseType.FP.name())
                .medAktørIdBarn(barnAktørId)
                .medAktørIdAnnenPart(annenPartAktørId)
                .build());
        var behandling = new Behandling.Builder()
                .medBehandlingId(behandlingId1)
                .medBehandlingStatus("AVSLU")
                .medSaksnummer(saksnummer)
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .build();
        repository.lagre(behandling);

        var behandlingId2 = 999L;
        repository.lagre(new Sak.Builder()
                .medSaksnummer(saksnummer)
                .medBehandlingId(String.valueOf(behandlingId2))
                .medFagsakStatus("OPPR")
                .medAktørId(aktørId.value())
                .medFagsakYtelseType(FagsakYtelseType.FP.name())
                .medAktørIdBarn(barnAktørId)
                .medAktørIdAnnenPart(null)
                .build());
        var behandling2 = new Behandling.Builder()
                .medBehandlingId(behandlingId2)
                .medBehandlingType(BehandlingType.ANNET)
                .medBehandlingStatus("AVSLU")
                .medSaksnummer(saksnummer)
                .build();
        repository.lagre(behandling2);

        var søknadsGrunnlag = new SøknadsGrunnlag.Builder()
                .brukerRolle("MORA")
                .dekningsgrad(100)
                .behandlingId(behandlingId1)
                .foreldreRettigheter(new SøknadsGrunnlagRettigheter(1L, null, true, null, false, false, true, true, true))
                .uføreGrunnlag(new UføreGrunnlag(behandlingId1, false))
                .build();
        repository.lagre(behandlingId1, søknadsGrunnlag);
        repository.lagre(behandlingId2, søknadsGrunnlag);

        repository.lagre(new FamilieHendelse(behandlingId1, 1, null, omsorgsovertakelse, omsorgsovertakelse));
        repository.lagre(new FamilieHendelse(behandlingId2, 1, null, omsorgsovertakelse, omsorgsovertakelse));

        var tjeneste = tjeneste(repository);
        var saker = tjeneste.hentFor(aktørId);

        assertThat(saker).hasSize(1);
    }

}
