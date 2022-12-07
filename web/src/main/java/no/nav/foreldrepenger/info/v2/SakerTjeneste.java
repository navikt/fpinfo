package no.nav.foreldrepenger.info.v2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.SøknadsperiodeEntitet;
import no.nav.foreldrepenger.info.datatyper.BehandlingType;
import no.nav.foreldrepenger.info.datatyper.BehandlingÅrsakType;
import no.nav.foreldrepenger.info.repository.Repository;


@ApplicationScoped
class SakerTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(SakerTjeneste.class);

    private Repository repository;

    @Inject
    public SakerTjeneste(Repository repository) {
        this.repository = repository;
    }

    SakerTjeneste() {
        //CDI
    }

    public Set<FpSak> hentFor(AktørId aktørId) {
        var sakList = repository.hentSak(aktørId.value());
        return sakList.stream()
                .filter(s -> s.getFagsakYtelseType().equals("FP"))
                .map(s -> new FpSakRef(new no.nav.foreldrepenger.info.v2.Saksnummer(s.getSaksnummer()),
                        s.getAktørIdAnnenPart() == null ? null : new AktørId(s.getAktørIdAnnenPart()),
                        s.getFagsakStatus(), s.getOpprettetTidspunkt()))
                //Får en sak per behandling fra repo
                .distinct()
                .flatMap(s -> hentFpSak(s).stream())
                .collect(Collectors.toSet());
    }

    private Optional<FpSak> hentFpSak(FpSakRef fpSak) {
        return gjeldendeVedtak(fpSak)
                .map(vedtakId -> sakMedVedtak(fpSak, vedtakId))
                .orElseGet(() -> sakUtenVedtak(fpSak));
    }

    private Optional<FpSak> sakUtenVedtak(FpSakRef fpSak) {
        var åbOpt = finnÅpenBehandling(fpSak.saksnummer());
        if (åbOpt.isEmpty()) {
            //Henlagte behandlinger
            return Optional.empty();
        }
        var åpenBehandling = åbOpt.get();
        var søknadsgrunnlagOpt = finnSøknadsgrunnlag(åpenBehandling.getBehandlingId());
        if (søknadsgrunnlagOpt.isEmpty()) {
            //Kommer hit hvis behandling uten søknad. Feks ved utsendelse av brev, eller papir punching
            return Optional.empty();
        }
        var søknadsgrunnlag = søknadsgrunnlagOpt.get();
        var tilhørerMor = tilhørerSakMor(søknadsgrunnlag);
        var annenPart = annenPart(fpSak).orElse(null);
        var familiehendelse = familiehendelse(søknadsgrunnlag);

        var barn = barn(fpSak.saksnummer());
        var rettighetType = rettighetTypeFraSøknad(søknadsgrunnlag);
        var sisteSøknadMottattDato = sisteSøknadMottattDato(åpenBehandling.getBehandlingId());
        return Optional.of(new FpSak(fpSak.saksnummer, false, sisteSøknadMottattDato.orElse(null), false,
                tilhørerMor, nullSafeEquals(søknadsgrunnlag.søknadMorUfør()), nullSafeEquals(søknadsgrunnlag.søknadHarAnnenForelderTilsvarendeRettEØS()),
                isØnskerJustertUttakVedFødsel(søknadsgrunnlag), rettighetType, annenPart, familiehendelse, null, map(åpenBehandling),
                barn, dekningsgrad(søknadsgrunnlag), fpSak.opprettetTidspunkt()));
    }

    private Optional<LocalDate> sisteSøknadMottattDato(Long behandlingId) {
        return repository.hentMottattDokument(behandlingId).stream()
                .filter(MottattDokument::erSøknad)
                .map(MottattDokument::getMottattDato)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder());
    }

    private RettighetType rettighetType(SøknadsGrunnlag søknadsGrunnlag) {
        if (søknadsGrunnlag.getAleneomsorg()) {
            return RettighetType.ALENEOMSORG;
        }
        return søknadsGrunnlag.getAnnenForelderRett() ? RettighetType.BEGGE_RETT : RettighetType.BARE_SØKER_RETT;
    }

    private RettighetType rettighetTypeFraSøknad(SøknadsGrunnlag søknadsGrunnlag) {
        if (søknadsGrunnlag.getAleneomsorgSøknad()) {
            return RettighetType.ALENEOMSORG;
        }
        return søknadsGrunnlag.getAnnenForelderRettSøknad() ? RettighetType.BEGGE_RETT : RettighetType.BARE_SØKER_RETT;
    }

    private Optional<FpSak> sakMedVedtak(FpSakRef fpSak, Long gjeldendeVedtakBehandlingId) {
        var søknadsgrunnlag = finnSøknadsgrunnlag(gjeldendeVedtakBehandlingId)
                .orElseThrow(() -> new IllegalStateException("Forventer søknadsgrunnlag på behandling"));
        var tilhørerMor = tilhørerSakMor(søknadsgrunnlag);
        var familiehendelse = familiehendelse(søknadsgrunnlag);
        var vedtaksperioder = hentVedtakPerioder(gjeldendeVedtakBehandlingId);
        var gjeldendeVedtak = new FpVedtak(vedtaksperioder);
        var sakAvsluttet = Objects.equals(fpSak.fagsakStatus(), "AVSLU");
        var kanSøkeOmEndring = gjeldendeVedtak.perioder().stream().anyMatch(p -> p.resultat().innvilget());
        var åpenBehandling = finnÅpenBehandling(fpSak.saksnummer());
        var annenPart = annenPart(fpSak).orElse(null);
        var barn = barn(fpSak.saksnummer());
        var sisteBehandling = åpenBehandling.map(Behandling::getBehandlingId).orElse(gjeldendeVedtakBehandlingId);
        var sisteSøknadMottattDato = sisteSøknadMottattDato(sisteBehandling);
        return Optional.of(new FpSak(fpSak.saksnummer, sakAvsluttet, sisteSøknadMottattDato.orElse(null), kanSøkeOmEndring, tilhørerMor,
                nullSafeEquals(søknadsgrunnlag.bekreftetMorUfør()), nullSafeEquals(søknadsgrunnlag.bekreftetHarAnnenForelderTilsvarendeRettEØS()),
                isØnskerJustertUttakVedFødsel(søknadsgrunnlag), rettighetType(søknadsgrunnlag), annenPart,
                familiehendelse, gjeldendeVedtak, åpenBehandling.map(this::map).orElse(null), barn, dekningsgrad(søknadsgrunnlag),
                fpSak.opprettetTidspunkt));
    }

    private boolean isØnskerJustertUttakVedFødsel(SøknadsGrunnlag søknadsgrunnlag) {
        return nullSafeEquals(søknadsgrunnlag.ønskerJustertUttakVedFødsel());
    }

    private boolean nullSafeEquals(Boolean b) {
        return Objects.equals(true, b);
    }

    private Set<AktørId> barn(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer) {
        return repository.hentBarn(new Saksnummer(saksnummer.value()))
                .stream().map(AktørId::new)
                .collect(Collectors.toSet());
    }

    private Optional<SøknadsGrunnlag> finnSøknadsgrunnlag(Long behandlingId) {
        return repository.hentSøknadsGrunnlag(behandlingId);
    }

    private static Familiehendelse familiehendelse(SøknadsGrunnlag søknadsgrunnlag) {
        return new Familiehendelse(søknadsgrunnlag.getFødselDato(), søknadsgrunnlag.getTermindato(),
                søknadsgrunnlag.getAntallBarn(), søknadsgrunnlag.getOmsorgsovertakelseDato());
    }

    private static Dekningsgrad dekningsgrad(SøknadsGrunnlag søknadsgrunnlag) {
        return Dekningsgrad.valueOf(søknadsgrunnlag.getDekningsgrad());
    }

    private static boolean tilhørerSakMor(SøknadsGrunnlag søknadsgrunnlag) {
        return Objects.equals(søknadsgrunnlag.getBrukerRolle(), "MORA");
    }

    private FpÅpenBehandling map(Behandling behandling) {
        var søknadsperioder = finnSøknadsperioder(behandling);
        return new FpÅpenBehandling(BehandlingTilstand.UNDER_BEHANDLING, søknadsperioder);
    }

    private List<UttakPeriode> finnSøknadsperioder(Behandling behandling) {
        if (behandling.getBehandlingType().equals(BehandlingType.FØRSTEGANGSBEHANDLING)) {
            //Støtter bare førstegangsbehandlinger nå
            return repository.hentSøknadsperioder(behandling.getBehandlingId())
                    .stream().map(this::map)
                    .toList();
        }
        return List.of();
    }

    private UttakPeriode map(SøknadsperiodeEntitet entitet) {
        //PeriodeResultat alltid null på søknadsperiode
        var kontoType = finnKontoType(entitet);
        var utsettelseÅrsak = mapUtsettelseÅrsak(entitet.getUtsettelseÅrsak());
        var oppholdÅrsak = mapOppholdÅrsak(entitet.getOppholdÅrsak());
        var overføringÅrsak = mapOverføringÅrsak(entitet.getOverføringÅrsak());
        var samtidigUttak = map(entitet.getSamtidigUttaksprosent());
        //frontend vil ikke ha detaljer om gradering ved samtidigUttak
        var gradering = samtidigUttak == null ? mapGradering(entitet) : null;
        return new UttakPeriode(entitet.getFom(), entitet.getTom(), kontoType, null,
                utsettelseÅrsak, oppholdÅrsak, overføringÅrsak, gradering, mapMorsAktivitet(entitet.getMorsAktivitet()),
                samtidigUttak, entitet.isFlerbarnsdager());
    }

    private static KontoType finnKontoType(SøknadsperiodeEntitet entitet) {
        return entitet.getTrekkonto().map(tk -> {
            if (tk.equals("ANNET")) {
                return null;
            }
            return KontoType.valueOf(tk);
        }).orElse(null);
    }

    private Gradering mapGradering(SøknadsperiodeEntitet entitet) {
        if (entitet.getArbeidstidprosent() == null || entitet.getArbeidstidprosent() <= 0) {
            return null;
        }
        var arbeidstidprosent = new Gradering.Arbeidstidprosent(BigDecimal.valueOf(entitet.getArbeidstidprosent()));
        var aktivitetType = mapAktivitetType(entitet);
        var type = new Aktivitet(aktivitetType, utledArbeidsgiver(entitet.getArbeidsgiverOrgnr(), entitet.getArbeidsgiverAktørId()));
        return new Gradering(arbeidstidprosent, type);
    }

    private Aktivitet.Type mapAktivitetType(SøknadsperiodeEntitet entitet) {
        if (entitet.isArbeidstaker()) {
            return Aktivitet.Type.ORDINÆRT_ARBEID;
        }
        if (entitet.isFrilanser()) {
            return Aktivitet.Type.FRILANS;
        }
        if (entitet.isSelvstendig()) {
            return Aktivitet.Type.SELVSTENDIG_NÆRINGSDRIVENDE;
        }
        throw new IllegalStateException("Ukjent aktivitettype for gradering");
    }

    private Optional<Long> gjeldendeVedtak(FpSakRef fpSak) {
        return repository.hentGjeldendeBehandling(new Saksnummer(fpSak.saksnummer().value()));
    }

    private Optional<Behandling> finnÅpenBehandling(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer) {
        return repository.hentTilknyttedeBehandlinger(saksnummer.value())
                .stream()
                .filter(b -> !b.erAvsluttet())
                .filter(b -> erRelevant(b))
                .max(Comparator.comparing(Behandling::getOpprettetTidspunkt));
    }

    private boolean erRelevant(Behandling behandling) {
        if (BehandlingType.FØRSTEGANGSBEHANDLING.equals(behandling.getBehandlingType())) {
            return erRelevantFørstegangsbehandling(behandling);
        }
        return erRelevantRevurdering(behandling);
    }

    private boolean erRelevantFørstegangsbehandling(Behandling behandling) {
        var søknadsGrunnlag = repository.hentSøknadsGrunnlag(behandling.getBehandlingId());
        if (søknadsGrunnlag.isEmpty()) {
            return false;
        }
        var dokumenter = repository.hentMottattDokument(behandling.getBehandlingId());
        return dokumenter.stream().anyMatch(MottattDokument::erSøknad);
    }

    private boolean erRelevantRevurdering(Behandling behandling) {
        return behandling.getÅrsaker()
                .stream()
                .anyMatch(å -> BehandlingÅrsakType.ENDRINGSSØKNAD.equals(å.getType()));
    }

    private Optional<AnnenPart> annenPart(FpSakRef fpSak) {
        if (fpSak.annenPart() == null) {
            return Optional.empty();
        }
        return Optional.of(new AnnenPart(fpSak.annenPart()));
    }

    private List<UttakPeriode> hentVedtakPerioder(Long behandlingId) {
        return repository.hentUttakPerioder(behandlingId)
                .stream()
                // flere arbeidsforhold gir flere perioder med samme tidsperiode. Selvbetjening frontend støtter ikke flere
                // arbeidsforhold per periode, så her velger vi den med høyest arbeidstidsprosent (for at gradering skal bli riktig).
                // Dette kan gi feil i noen case der feks aktivitene har forskjellig trekkonto
                .sorted((o1, o2) -> o1.getArbeidstidprosent() != null && o2.getArbeidstidprosent() != null
                        ? o2.getArbeidstidprosent().compareTo(o1.getArbeidstidprosent()) : 0)
                .filter(distinct(no.nav.foreldrepenger.info.UttakPeriode::getFom))
                .sorted(Comparator.comparing(no.nav.foreldrepenger.info.UttakPeriode::getFom))
                .map(p -> map(p, behandlingId))
                .collect(Collectors.toList());
    }

    private UttakPeriode map(no.nav.foreldrepenger.info.UttakPeriode p, Long behandlingId) {
        var trekkerMinsterett = !Set.of("2004", "2033").contains(p.getPeriodeResultatÅrsak());
        var trekkerDager = p.getTrekkdager() != null && p.getTrekkdager().compareTo(BigDecimal.ZERO) >= 1;
        var årsak = mapÅrsak(p.getPeriodeResultatÅrsak());
        var resultat = new UttakPeriodeResultat("INNVILGET".equals(p.getPeriodeResultatType()), trekkerMinsterett,
                trekkerDager, årsak);
        var samtidigUttaksprosent = p.getSamtidigUttaksprosent();
        var samtidigUttak = map(samtidigUttaksprosent);
        if (p.getSamtidigUttak() && samtidigUttaksprosent == null) {
            LOG.warn("Samtidig uttak uten samtidig uttaksprosent. Behandling {}", behandlingId);
        }
        if (p.getSamtidigUttak() && samtidigUttaksprosent != null && p.getUtbetalingsprosent() != null && !p.getUtbetalingsprosent().equals(samtidigUttaksprosent)) {
            LOG.warn("Samtidig uttak der prosent er ulik utbetalingsprosent. Behandling {}", behandlingId);
        }

        //frontend vil ikke ha detaljer om gradering ved samtidigUttak
        final Gradering gradering;
        if (samtidigUttak == null && p.getGraderingInnvilget()) {
            var arbeidsgiver = utledArbeidsgiver(p);
            var aktivitetType = mapAktivitetType(p.getUttakArbeidType());
            gradering = new Gradering(BigDecimal.valueOf(p.getArbeidstidprosent()), new Aktivitet(aktivitetType,
                    arbeidsgiver));
        } else {
            gradering = null;
        }
        return new UttakPeriode(p.getFom(), p.getTom(), mapKontotype(p), resultat, mapUtsettelseÅrsak(p.getUttakUtsettelseType()),
                mapOppholdÅrsak(p.getOppholdÅrsak()), mapOverføringÅrsak(p.getOverføringÅrsak()), gradering, mapMorsAktivitet(p.getMorsAktivitet()),
                samtidigUttak, p.getFlerbarnsdager());
    }

    private UttakPeriodeResultat.Årsak mapÅrsak(String periodeResultatÅrsak) {
        return switch (periodeResultatÅrsak) {
            case "4005", "4102" -> UttakPeriodeResultat.Årsak.AVSLAG_HULL_MELLOM_FORELDRENES_PERIODER;
            default -> UttakPeriodeResultat.Årsak.ANNET;
        };
    }

    private Aktivitet.Type mapAktivitetType(String type) {
        if (type == null) {
            return null;
        }
        return Aktivitet.Type.valueOf(type);
    }

    private Arbeidsgiver utledArbeidsgiver(no.nav.foreldrepenger.info.UttakPeriode p) {
        var orgnr = p.getArbeidsgiverOrgnr();
        var arbeidsgiverAktørId = p.getArbeidsgiverAktørId();
        return utledArbeidsgiver(orgnr, arbeidsgiverAktørId);
    }

    private static Arbeidsgiver utledArbeidsgiver(String orgnr, String arbeidsgiverAktørId) {
        if (orgnr != null) {
            return new Arbeidsgiver(orgnr, Arbeidsgiver.ArbeidsgiverType.ORGANISASJON);
        } else if (arbeidsgiverAktørId != null) {
            return new Arbeidsgiver(arbeidsgiverAktørId, Arbeidsgiver.ArbeidsgiverType.PRIVAT);
        }
        return null;
    }

    private KontoType mapKontotype(no.nav.foreldrepenger.info.UttakPeriode p) {
        var trekkonto = p.getTrekkonto();
        return trekkonto == null ? null : KontoType.valueOf(trekkonto);
    }

    private SamtidigUttak map(Long samtidigUttaksprosent) {
        return samtidigUttaksprosent != null
                && samtidigUttaksprosent.compareTo(0L) > 0 ? new SamtidigUttak(BigDecimal.valueOf(samtidigUttaksprosent)) : null;
    }

    private MorsAktivitet mapMorsAktivitet(no.nav.foreldrepenger.info.datatyper.MorsAktivitet morsAktivitet) {
        if (morsAktivitet == null) {
            return null;
        }
        return MorsAktivitet.valueOf(morsAktivitet.name());
    }

    private UtsettelseÅrsak mapUtsettelseÅrsak(String uttakUtsettelseType) {
        if (uttakUtsettelseType == null) {
            return null;
        }
        return switch (uttakUtsettelseType) {
            case "-" -> null;
            case "HV_OVELSE" -> UtsettelseÅrsak.HV_ØVELSE;
            case "SYKDOM_SKADE", "SYKDOM" -> UtsettelseÅrsak.SØKER_SYKDOM;
            case "INSTITUSJONSOPPHOLD_SØKER" -> UtsettelseÅrsak.SØKER_INNLAGT;
            case "INSTITUSJONSOPPHOLD_BARNET" -> UtsettelseÅrsak.BARN_INNLAGT;
            default -> UtsettelseÅrsak.valueOf(uttakUtsettelseType);
        };
    }

    private OppholdÅrsak mapOppholdÅrsak(String oppholdÅrsak) {
        if (oppholdÅrsak == null) {
            return null;
        }
        return switch (oppholdÅrsak) {
            case "-" -> null;
            case "UTTAK_MØDREKVOTE_ANNEN_FORELDER" -> OppholdÅrsak.MØDREKVOTE_ANNEN_FORELDER;
            case "UTTAK_FEDREKVOTE_ANNEN_FORELDER" -> OppholdÅrsak.FEDREKVOTE_ANNEN_FORELDER;
            case "UTTAK_FELLESP_ANNEN_FORELDER" -> OppholdÅrsak.FELLESPERIODE_ANNEN_FORELDER;
            case "UTTAK_FORELDREPENGER_ANNEN_FORELDER" -> OppholdÅrsak.FORELDREPENGER_ANNEN_FORELDER;
            default -> OppholdÅrsak.valueOf(oppholdÅrsak);
        };
    }

    private OverføringÅrsak mapOverføringÅrsak(String overføringÅrsak) {
        if (overføringÅrsak == null || "-".equals(overføringÅrsak)) {
            return null;
        }
        return OverføringÅrsak.valueOf(overføringÅrsak);
    }

    private static <T> Predicate<T> distinct(Function<? super T, ?> key) {
        ConcurrentHashMap.KeySetView<Object, Boolean> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(key.apply(t));
    }

    private record FpSakRef(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer,
                            AktørId annenPart,
                            String fagsakStatus,
                            LocalDateTime opprettetTidspunkt) {
    }
}
