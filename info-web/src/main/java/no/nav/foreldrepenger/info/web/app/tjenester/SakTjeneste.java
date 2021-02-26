package no.nav.foreldrepenger.info.web.app.tjenester;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.Sak;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingType;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingÅrsakType;
import no.nav.foreldrepenger.info.repository.Repository;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.AktørIdDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.SakDto;

@ApplicationScoped
public class SakTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(SakTjeneste.class);

    private Repository repository;

    @Inject
    public SakTjeneste(Repository repository) {
        this.repository = repository;
    }

    public SakTjeneste() {
    }

    public List<SakDto> hentSak(AktørIdDto aktørIdDto, String linkPathBehandling, String linkPathUttaksplan) {
        LOG.info("Henter sakstatus");
        var saker = mapTilSakStatusDtoListe(repository.hentSak(aktørIdDto.getAktørId()), linkPathBehandling, linkPathUttaksplan);
        LOG.info("Fant {} statuser", saker.size());
        return saker;
    }

    private List<SakDto> mapTilSakStatusDtoListe(List<Sak> sakListe,
            String linkPathBehandling,
            String linkPathUttaksplan) {
        return sakListe.stream()
                .filter(distinct(Sak::getSaksnummer))
                .map(sak -> {
                    var dto = SakDto.fraDomene(sak, harMottattEndringssøknad(sak));

                    // Alle barn som gjelder denne saken, kan spenne over flere behandlinger uansett
                    // status
                    sakListe.stream()
                            .filter(e -> sak.getSaksnummer().equals(e.getSaksnummer()))
                            .forEach(e -> dto.leggTilBarn(e.getAktørIdBarn()));

                    repository.hentTilknyttedeBehandlinger(sak.getSaksnummer())
                            .stream()
//                    .filter(this::erRelevant) //TODO må brukes ved omskriving.
                            .forEach(elem -> {
                                String href = linkPathBehandling + elem.getBehandlingId();
                                dto.leggTilLenke(href, "behandlinger");
                            });

                    repository.hentFagsakRelasjon(sak.getSaksnummer()).ifPresent(up -> {
                        dto.leggTilLenke(linkPathUttaksplan + sak.getSaksnummer(), "uttaksplan");
                    });
                    return dto;
                }).collect(Collectors.toList());
    }

    private boolean erRelevant(Behandling behandling) {
        return Objects.equals(behandling.getBehandlingType(), BehandlingType.FØRSTEGANGSBEHANDLING)
                || erRevurderingMedEndringssøknad(behandling);
    }

    private boolean erRevurderingMedEndringssøknad(Behandling behandling) {
        return Objects.equals(behandling.getBehandlingType(), BehandlingType.REVURDERING)
                && behandlingHarMottattEndringssøknad(behandling);
    }

    private boolean harMottattEndringssøknad(Sak sak) {
        return repository.hentTilknyttedeBehandlinger(sak.getSaksnummer())
                .stream()
                .anyMatch(this::behandlingHarMottattEndringssøknad);
    }

    private boolean behandlingHarMottattEndringssøknad(Behandling behandling) {
        return behandling.getÅrsaker()
                .stream()
                .anyMatch(behandlingÅrsak -> Objects.equals(behandlingÅrsak.getType(), BehandlingÅrsakType.ENDRINGSSØKNAD));
    }

    private static <T> Predicate<T> distinct(Function<? super T, ?> key) {
        ConcurrentHashMap.KeySetView<Object, Boolean> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(key.apply(t));
    }
}
