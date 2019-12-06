package no.nav.foreldrepenger.info.tjenester;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.info.domene.Saksnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import no.nav.foreldrepenger.info.domene.Aksjonspunkt;
import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.domene.SakStatus;
import no.nav.foreldrepenger.info.domene.UttakPeriode;
import no.nav.foreldrepenger.info.domene.FagsakRelasjon;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingResultatType;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingStatus;
import no.nav.foreldrepenger.info.repository.DokumentForsendelseRepository;
import no.nav.foreldrepenger.info.tjenester.dto.AktørAnnenPartDto;
import no.nav.foreldrepenger.info.tjenester.dto.AktørIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.tjenester.dto.BehandlingIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseStatus;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseStatusDataDTO;
import no.nav.foreldrepenger.info.tjenester.dto.SakStatusDto;
import no.nav.foreldrepenger.info.tjenester.dto.SaksnummerDto;
import no.nav.foreldrepenger.info.tjenester.dto.SøknadXmlDto;
import no.nav.foreldrepenger.info.tjenester.dto.SøknadsGrunnlagDto;
import no.nav.foreldrepenger.info.tjenester.dto.UttaksPeriodeDto;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;
import no.nav.vedtak.felles.jpa.TomtResultatException;

@ApplicationScoped
class DokumentforsendelseTjenesteImpl implements DokumentforsendelseTjeneste {

    private static final Logger LOGGER = LoggerFactory.getLogger(DokumentforsendelseTjenesteImpl.class);
    private DokumentForsendelseRepository dokumentForsendelseRepository;


    public DokumentforsendelseTjenesteImpl() {
        // FOR CDI Proxy
    }

    @Inject
    public DokumentforsendelseTjenesteImpl(DokumentForsendelseRepository dokumentForsendelseRepository) {
        this.dokumentForsendelseRepository = dokumentForsendelseRepository;
    }

    @Override
    public BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto, String linkPathSøknad) {
        Long behandlingId = behandlingIdDto.getBehandlingId();
        Behandling behandling = dokumentForsendelseRepository.hentBehandling(behandlingId);
        BehandlingDto dto = BehandlingDto.fraDomene(behandling);
        List<MottattDokument> inntektsmeldinger = dokumentForsendelseRepository.hentInntektsmeldinger(behandlingId);
        dto.setInntektsmeldinger(inntektsmeldinger.stream().map(MottattDokument::getJournalpostId).collect(Collectors.toList()));
        if (dokumentForsendelseRepository.harSøknad(behandlingId)) {
            dto.leggTilLenke(linkPathSøknad + behandlingId, "søknad");
        }
        return dto;
    }

    @Override
    public SøknadXmlDto hentSøknadXml(BehandlingIdDto behandlingIdDto) {
        LOGGER.info("henter søknad for behandling {}", behandlingIdDto.getBehandlingId());

        Long behandlingId = behandlingIdDto.getBehandlingId();
        List<MottattDokument> dokumenter = dokumentForsendelseRepository.hentSøknadXml(behandlingId)
                .stream().filter(MottattDokument::erSøknad).collect(Collectors.toList());
        if (dokumenter.isEmpty()) {
            throw DokumentforsendelseFeil.FACTORY.fantIkkeSøknadForBehandling(behandlingId).toException();
        }
        SøknadXmlDto søknad = mapTilSøknadXml(dokumenter);
        LOGGER.info("returnerer søknad for behandling {}", behandlingIdDto.getBehandlingId());
        return søknad;
    }

    @Override
    public Optional<ForsendelseStatusDataDTO> hentStatusInformasjon(ForsendelseIdDto forsendelseIdDto) {
        UUID forsendelseId = forsendelseIdDto.getForsendelseId();

        LOGGER.info("Henter status for forsendelse {}", forsendelseId);

        List<MottattDokument> mottatteDokumenter = dokumentForsendelseRepository.hentMottatteDokumenter(forsendelseId);
        if (mottatteDokumenter.isEmpty()) {
            LOGGER.info("Fant ingen dokumenter for forsendelse {}", forsendelseId);
            return Optional.empty();
        }
        Set<Long> behandlingsIder = mottatteDokumenter.stream().map(MottattDokument::getBehandlingId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (behandlingsIder.isEmpty()) {
            LOGGER.info("Returnerer MOTTATT for forsendelse {}", forsendelseId);
            return Optional.of(new ForsendelseStatusDataDTO(ForsendelseStatus.MOTTATT));
        }
        if (behandlingsIder.size() > 1) {
            throw DokumentforsendelseTjenesteFeil.FACTORY.flereBehandlingerForForsendelsen(behandlingsIder, forsendelseId).toException();
        }

        Behandling behandling = dokumentForsendelseRepository.hentBehandling(behandlingsIder.iterator().next());
        ForsendelseStatusDataDTO forsendelseStatusDataDTO = getForsendelseStatusDataDTO(behandling, forsendelseId);
        LOGGER.info("Returnerer behandling {} for forsendelse {}", behandling.getBehandlingId(), forsendelseId);
        return Optional.ofNullable(forsendelseStatusDataDTO);
    }

    @Override
    public List<UttaksPeriodeDto> hentFellesUttaksplan(SaksnummerDto saksnummerDto, boolean erAnnenPart) {
        String saksnummer = saksnummerDto.getSaksnummer();
        LOGGER.info("Henter felles uttaksplan basert på saksnummer {}", saksnummerDto.getSaksnummer());
        Optional<FagsakRelasjon> fagsakRelasjonOptional = dokumentForsendelseRepository.hentFagsakRelasjon(saksnummerDto.getSaksnummer());
        if (fagsakRelasjonOptional.isEmpty()) {
            LOGGER.info("Fant ingen uttaksplan for saksnummer {}", saksnummerDto.getSaksnummer());
            return Collections.emptyList();
        }

        FagsakRelasjon fagsakRelasjon = fagsakRelasjonOptional.get();
        List<UttaksPeriodeDto> fellesPlan = hentUttakPerioder(fagsakRelasjon.getSaksnummer()).stream()
                .map(up -> UttaksPeriodeDto.fraDomene(saksnummer, up, erAnnenPart))
                .collect(Collectors.toList());

        Optional<Saksnummer> annenPartFagsak = fagsakRelasjon.finnSaksnummerAnnenpart();
        if (annenPartFagsak.isPresent()) {
            hentUttakPerioder(annenPartFagsak.get()).stream()
                    .map(up -> UttaksPeriodeDto.fraDomene(saksnummer, up, !erAnnenPart))
                    .forEach(fellesPlan::add);
        }
        LOGGER.info("Returnererer uttaksplan med {} perioder for saksnummer {}", fellesPlan.size(), saksnummerDto.getSaksnummer());
        return fellesPlan;
    }

    private List<UttakPeriode> hentUttakPerioder(Saksnummer saksnummer) {
        var gjeldendeBehandling = getGjeldendeBehandlingId(saksnummer);
        return gjeldendeBehandling
                .map(gb -> dokumentForsendelseRepository.hentUttakPerioder(gb))
                .orElse(List.of());
    }

    @Override
    public SøknadsGrunnlagDto hentSøknadsgrunnlag(SaksnummerDto saksnummerDto, boolean erAnnenPart) {
        var saksnummer = new Saksnummer(saksnummerDto.getSaksnummer());
        var gjeldendeBehandling = getGjeldendeBehandlingId(saksnummer);
        if (gjeldendeBehandling.isEmpty()) {
            throw DokumentforsendelseFeil.FACTORY.kanIkkeLageSøknadsgrunnlag(saksnummer.toString()).toException();
        }
        var søknadsGrunnlag = dokumentForsendelseRepository.hentSøknadsGrunnlag(gjeldendeBehandling.get());
        var uttaksplan = hentFellesUttaksplan(saksnummerDto, erAnnenPart);
        LOGGER.info("Henter søknadsgrunnlag for saksnummer {}. Gjeldende vedtak er {}. Antall uttaksperioder i felles plan er {}.",
                saksnummer.asString(), gjeldendeBehandling.get(), uttaksplan.size());
        return SøknadsGrunnlagDto
                .fraDomene(saksnummer, søknadsGrunnlag)
                .medUttaksPerioder(uttaksplan);

    }

    private Optional<Long> getGjeldendeBehandlingId(Saksnummer saksnummer) {
        var behandlingId = dokumentForsendelseRepository.hentGjeldendeBehandling(saksnummer);
        if (behandlingId.isEmpty()) {
            LOGGER.info("Finner ingen gjeldende behandlingId for saksnummer " + saksnummer.asString());
        } else {
            LOGGER.info("Bruker behandlingId: " + behandlingId.get());
        }
        return behandlingId;
    }

    @Override
    public SøknadsGrunnlagDto hentSøknadAnnenPart(AktørIdDto aktørIdBrukerDto, AktørAnnenPartDto aktørAnnenPartDto) {
        Optional<SakStatus> sakAnnenPart = dokumentForsendelseRepository.finnNyesteSakForAnnenPart(aktørIdBrukerDto.getAktørId(), aktørAnnenPartDto.getAnnenPartAktørId());
        if (sakAnnenPart.isEmpty()) {
            throw DokumentforsendelseFeil.FACTORY.kanIkkeLageSøknadsgrunnlag(aktørAnnenPartDto.toString()).toException();
        }
        SakStatus sak = sakAnnenPart.get();
        var grunnlag = hentSøknadsgrunnlag(new SaksnummerDto(sak.getSaksnummer()), true);
        grunnlag.setAnnenPartFraSak(sak.getAktørIdAnnenPart());
        return grunnlag;
    }

    @Override
    public List<SakStatusDto> hentSakStatus(AktørIdDto aktørIdDto, String linkPathBehandling, String linkPathUttaksplan) {
        LOGGER.info("Henter sakstatus");
        List<SakStatus> sakListe = dokumentForsendelseRepository.hentSakStatus(aktørIdDto.getAktørId());
        List<SakStatusDto> statusListe = mapTilSakStatusDtoListe(sakListe, linkPathBehandling, linkPathUttaksplan);
        LOGGER.info("Fant {} statuser", statusListe.size());
        return statusListe;
    }

    private Set<Long> hentIkkeHenlagteBehandlingIder(String saksnummer) {
        List<Behandling> behandlinger = dokumentForsendelseRepository.hentTilknyttedeBehandlinger(saksnummer);

        return behandlinger.stream()
                .filter(behandling -> !behandling.erHenlagt())
                .map(Behandling::getBehandlingId)
                .collect(Collectors.toSet());
    }

    private ForsendelseStatusDataDTO getForsendelseStatusDataDTO(Behandling behandling, UUID forsendelseId) {
        ForsendelseStatusDataDTO forsendelseStatusDataDTO;
        String behandlingStatus = behandling.getBehandlingStatus();
        if (behandlingStatus.equals(BehandlingStatus.AVSLUTTET.getVerdi()) || behandlingStatus.equals(BehandlingStatus.IVERKSETTER_VEDTAK.getVerdi())) {

            String resultat = behandling.getBehandlingResultatType();
            if (resultat.equals(BehandlingResultatType.INNVILGET.getVerdi())
                    || resultat.equals(BehandlingResultatType.FORELDREPENGER_ENDRET.getVerdi())
                    || resultat.equals(BehandlingResultatType.INGEN_ENDRING.getVerdi())) {
                forsendelseStatusDataDTO = new ForsendelseStatusDataDTO(ForsendelseStatus.INNVILGET);
            } else if (resultat.equals(BehandlingResultatType.AVSLÅTT.getVerdi())) {
                forsendelseStatusDataDTO = new ForsendelseStatusDataDTO(ForsendelseStatus.AVSLÅTT);
            } else if (resultat.equals(BehandlingResultatType.MERGET_OG_HENLAGT.getVerdi())) {
                // FIXME: finnes ikke funksjonalitet for å håndtere MERGET_OG_HENLAGT
                LOGGER.info("Behandlingsresultat er {}, fpinfo ser ikke videre på denne", BehandlingResultatType.MERGET_OG_HENLAGT.getVerdi());
                forsendelseStatusDataDTO = null;
            } else {
                throw DokumentforsendelseTjenesteFeil.FACTORY.ugyldigBehandlingResultat(forsendelseId).toException();
            }

        } else {
            List<Aksjonspunkt> aksjonspunkt = behandling.getÅpneAksjonspunkter();
            if (aksjonspunkt.isEmpty()) {
                forsendelseStatusDataDTO = new ForsendelseStatusDataDTO(ForsendelseStatus.PÅGÅR);
            } else {
                forsendelseStatusDataDTO = new ForsendelseStatusDataDTO(ForsendelseStatus.PÅ_VENT);
            }
        }
        return forsendelseStatusDataDTO;
    }

    private List<SakStatusDto> mapTilSakStatusDtoListe(List<SakStatus> sakListe, String linkPathBehandling, String linkPathUttaksplan) {
        return sakListe.stream().filter(distinct(SakStatus::getSaksnummer)).map(sak -> {
            SakStatusDto dto = SakStatusDto.fraDomene(sak);

            // Alle barn som gjelder denne saken, kan spenne over flere behandlinger uansett status
            sakListe.stream()
                    .filter(e -> sak.getSaksnummer().equals(e.getSaksnummer()))
                    .forEach(e -> dto.leggTilBarn(e.getAktørIdBarn()));

            hentIkkeHenlagteBehandlingIder(sak.getSaksnummer()).forEach(elem -> {
                String href = linkPathBehandling + elem;
                dto.leggTilLenke(href, "behandlinger");
            });

            dokumentForsendelseRepository.hentFagsakRelasjon(sak.getSaksnummer()).ifPresent(up -> {
                String href = linkPathUttaksplan + sak.getSaksnummer();
                dto.leggTilLenke(href, "uttaksplan");
            });
            return dto;
        }).collect(Collectors.toList());
    }

    private SøknadXmlDto mapTilSøknadXml(List<MottattDokument> dokumenter) {
        // Noen søknader er lagret i to innslag hvor ett innslag har XML payload og det andre har journalpostId
        if (dokumenter.size() == 2) {
            return SøknadXmlDto.fraDomene(dokumenter.get(0), dokumenter.get(1));
        }
        return SøknadXmlDto.fraDomene(dokumenter.get(0));
    }

    interface DokumentforsendelseFeil extends DeklarerteFeil {
        DokumentforsendelseFeil FACTORY = FeilFactory.create(DokumentforsendelseFeil.class);

        @TekniskFeil(feilkode = "FP-782451", feilmelding = "Fant ingen søknader for behandling: %s", logLevel = LogLevel.WARN, exceptionClass = TomtResultatException.class)
        Feil fantIkkeSøknadForBehandling(Long behandlingId);

        @TekniskFeil(feilkode = "FP-356326", feilmelding = "Fant ingen vedtak for behandling: %s", logLevel = LogLevel.INFO, exceptionClass = TomtResultatException.class)
        Feil fantIkkeVedtakForBehandling(Long behandlingId);

        @TekniskFeil(feilkode = "FP-356327", feilmelding = "Fant ingen ferdige behandlinger, kan ikke bygge søknadsgrunnlag: %s", logLevel = LogLevel.INFO, exceptionClass = TomtResultatException.class)
        Feil kanIkkeLageSøknadsgrunnlag(String markør);
    }

    private static <T> Predicate<T> distinct(Function<? super T, ?> key) {
        ConcurrentHashMap.KeySetView<Object, Boolean> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(key.apply(t));
    }
}
