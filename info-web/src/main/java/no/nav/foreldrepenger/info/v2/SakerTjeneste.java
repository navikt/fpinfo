package no.nav.foreldrepenger.info.v2;

import java.math.BigDecimal;
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

import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.v2.persondetaljer.AktørId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.UttakPeriode;
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

    public Saker hentFor(AktørId aktørId) {
        var sakList = repository.hentSak(aktørId.value());
        var fpSaker = sakList.stream()
                .filter(s -> s.getFagsakYtelseType().equals("FP"))
                .map(s -> new FpSakRef(new no.nav.foreldrepenger.info.v2.Saksnummer(s.getSaksnummer()),
                        s.getAktørIdAnnenPart() == null ? null : new AktørId(s.getAktørIdAnnenPart()),
                        s.getFagsakStatus(), s.getOpprettetTidspunkt()))
                //Får en sak per behandling fra repo
                .distinct()
                .flatMap(s -> hentFpSak(s).stream())
                .collect(Collectors.toSet());
        return new Saker(fpSaker, Set.of(), Set.of());
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
        return Optional.of(new FpSak(fpSak.saksnummer, false, false, tilhørerMor,
                false, rettighetType(søknadsgrunnlag), annenPart, familiehendelse, null, map(åpenBehandling),
                barn, dekningsgrad(søknadsgrunnlag), åbOpt.get().getOpprettetTidspunkt())); //TODO: fiks opprettettidspunkt
    }

    private RettighetType rettighetType(SøknadsGrunnlag søknadsGrunnlag) {
        if (søknadsGrunnlag.getAleneomsorg()) {
            return RettighetType.ALENEOMSORG;
        }
        return søknadsGrunnlag.getAnnenForelderRett() ? RettighetType.BEGGE_RETT : RettighetType.BARE_SØKER_RETT;
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
        var åpenBehandling = åpenBehandling(fpSak);
        var annenPart = annenPart(fpSak).orElse(null);
        var barn = barn(fpSak.saksnummer());
        return Optional.of(new FpSak(fpSak.saksnummer, sakAvsluttet, kanSøkeOmEndring, tilhørerMor, false,
                rettighetType(søknadsgrunnlag), annenPart, familiehendelse, gjeldendeVedtak, åpenBehandling.orElse(null), barn, dekningsgrad(søknadsgrunnlag),
                fpSak.opprettetTidspunkt));
    }

    private Set<PersonDetaljer> barn(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer) {
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

    private Optional<FpÅpenBehandling> åpenBehandling(FpSakRef fpSak) {
        return finnÅpenBehandling(fpSak.saksnummer()).map(this::map);
    }

    private FpÅpenBehandling map(Behandling behandling) {
        //TODO palfi
        return new FpÅpenBehandling(BehandlingTilstand.UNDER_BEHANDLING, Set.of());
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
        //TODO palfi køet behandlinger

        //TODO palfi må også ha med revurderinger der det er sendt varsel til bruker.
        // Kan sjekke tabellen BEHANDLING_DOKUMENT i databasen om det finnes et bestilt dokument
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

    private List<VedtakPeriode> hentVedtakPerioder(Long behandlingId) {
        return repository.hentUttakPerioder(behandlingId)
                .stream()
                // flere arbeidsforhold gir flere perioder med samme tidsperiode. Selvbetjening frontend støtter ikke flere
                // arbeidsforhold per periode, så her velger vi en tilfeldig den med høyest arbeidstidsprosent (for at gradering skal bli riktig).
                // Dette kan gi feil i noen case der feks aktivitene har forskjellig trekkonto
                .sorted((o1, o2) -> o1.getArbeidstidprosent() != null && o2.getArbeidstidprosent() != null
                        ? o2.getArbeidstidprosent().compareTo(o1.getArbeidstidprosent()) : 0)
                .filter(distinct(UttakPeriode::getFom))
                .sorted(Comparator.comparing(UttakPeriode::getFom))
                .map(this::map)
                .collect(Collectors.toList());
    }

    private VedtakPeriode map(UttakPeriode p) {
        var resultat = new VedtakPeriodeResultat("INNVILGET".equals(p.getPeriodeResultatType()));
        var samtidigUttaksprosent = p.getSamtidigUttaksprosent();
        var samtidigUttak = map(samtidigUttaksprosent);

        //frontend vil ikke ha detaljer om gradering ved samtidigUttak
        final Gradering gradering;
        if (samtidigUttak == null) {
            gradering = p.getGraderingInnvilget() ? new Gradering(BigDecimal.valueOf(p.getArbeidstidprosent())) : null;
        } else {
            gradering = null;
        }
        return new VedtakPeriode(p.getFom(), p.getTom(), KontoType.valueOf(p.getTrekkonto()), resultat, mapUtsettelseÅrsak(p.getUttakUtsettelseType()),
                mapOppholdÅrsak(p.getOppholdÅrsak()), mapOverføringÅrsak(p.getOverføringÅrsak()), gradering, mapMorsAktivitet(p.getMorsAktivitet()),
                samtidigUttak, p.getFlerbarnsdager());
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
            case "SYKDOM_SKADE" -> UtsettelseÅrsak.SØKER_SYKDOM;
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

    private static record FpSakRef(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer,
                                   AktørId annenPart,
                                   String fagsakStatus,
                                   LocalDateTime opprettetTidspunkt) {
    }
}
