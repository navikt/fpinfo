package no.nav.foreldrepenger.info.web.app.tjenester;

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

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.domene.Sak;
import no.nav.foreldrepenger.info.domene.Saksnummer;
import no.nav.foreldrepenger.info.repository.Repository;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.AktørIdDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.BehandlingIdDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.SakDto;

@ApplicationScoped
public class BehandlingTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingTjeneste.class);

    private SøknadTjeneste søknadTjeneste;
    private Repository repository;

    @Inject
    public BehandlingTjeneste(SøknadTjeneste søknadTjeneste, Repository repository) {
        this.søknadTjeneste = søknadTjeneste;
        this.repository = repository;
    }

    BehandlingTjeneste() {
        // FOR CDI Proxy
    }

    public BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto, String linkPathSøknad) {
        var behandlingId = behandlingIdDto.getBehandlingId();
        var behandling = repository.hentBehandling(behandlingId);
        var dto = BehandlingDto.fraDomene(behandling);
        if (harSøknad(behandlingIdDto)) {
            dto.leggTilLenke(linkPathSøknad + behandlingId, "søknad");
        }
        var inntektsmeldinger = repository.hentInntektsmeldinger(behandlingId);
        dto.setInntektsmeldinger(journalPostIdr(inntektsmeldinger));
        return dto;
    }

    private List<String> journalPostIdr(List<MottattDokument> inntektsmeldinger) {
        return inntektsmeldinger.stream().map(MottattDokument::getJournalpostId).collect(Collectors.toList());
    }

    private boolean harSøknad(BehandlingIdDto behandlingIdDto) {
        return søknadTjeneste.harSøknad(behandlingIdDto.getBehandlingId());
    }

    public Optional<Long> getGjeldendeBehandlingId(Saksnummer saksnummer) {
        var behandlingId = repository.hentGjeldendeBehandling(saksnummer);
        if (behandlingId.isEmpty()) {
            LOG.info("Finner ingen gjeldende behandlingId for saksnummer " + saksnummer.asString());
        } else {
            LOG.info("Bruker gjeldende behandlingId: " + behandlingId.get());
        }
        return behandlingId;
    }

    public List<SakDto> hentSak(AktørIdDto aktørIdDto, String linkPathBehandling, String linkPathUttaksplan) {
        LOG.info("Henter sakstatus");
        var sakListe = repository.hentSakStatus(aktørIdDto.getAktørId());
        var statusListe = mapTilSakStatusDtoListe(sakListe, linkPathBehandling, linkPathUttaksplan);
        LOG.info("Fant {} statuser", statusListe.size());
        return statusListe;
    }

    private Set<Long> hentBehandlingIder(String saksnummer) {
        var behandlinger = repository.hentTilknyttedeBehandlinger(saksnummer);
        return behandlinger.stream().map(Behandling::getBehandlingId).collect(Collectors.toSet());
    }

    private List<SakDto> mapTilSakStatusDtoListe(List<Sak> sakListe,
                                                 String linkPathBehandling,
                                                 String linkPathUttaksplan) {
        return sakListe.stream().filter(distinct(Sak::getSaksnummer)).map(sak -> {
            var dto = SakDto.fraDomene(sak, harMottattEndringssøknad(sak));

            // Alle barn som gjelder denne saken, kan spenne over flere behandlinger uansett
            // status
            sakListe.stream()
                    .filter(e -> sak.getSaksnummer().equals(e.getSaksnummer()))
                    .forEach(e -> dto.leggTilBarn(e.getAktørIdBarn()));

            hentBehandlingIder(sak.getSaksnummer()).forEach(elem -> {
                String href = linkPathBehandling + elem;
                dto.leggTilLenke(href, "behandlinger");
            });

            repository.hentFagsakRelasjon(sak.getSaksnummer()).ifPresent(up -> {
                var href = linkPathUttaksplan + sak.getSaksnummer();
                dto.leggTilLenke(href, "uttaksplan");
            });
            return dto;
        }).collect(Collectors.toList());
    }

    private boolean harMottattEndringssøknad(Sak sak) {
        return repository.hentTilknyttedeBehandlinger(sak.getSaksnummer())
                .stream()
                .anyMatch(behandling -> Objects.equals(behandling.getBehandlingÅrsak(), "RE-END-FRA-BRUKER"));
    }

    private static <T> Predicate<T> distinct(Function<? super T, ?> key) {
        ConcurrentHashMap.KeySetView<Object, Boolean> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(key.apply(t));
    }
}
