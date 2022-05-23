package no.nav.foreldrepenger.info.app.tjenester;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.app.tjenester.dto.AktørIdDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.SakDto;
import no.nav.foreldrepenger.info.datatyper.BehandlingÅrsakType;
import no.nav.foreldrepenger.info.repository.Repository;

@Dependent
public class SakTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(SakTjeneste.class);

    private final Repository repository;

    @Inject
    public SakTjeneste(Repository repository) {
        this.repository = repository;
    }

    public List<SakDto> hentSak(AktørIdDto aktørIdDto, String linkPathBehandling, String linkPathUttaksplan) {
        LOG.trace("Henter sakstatus");
        var sakListe = repository.hentSak(aktørIdDto.aktørId());
        var statusListe = mapTilSakStatusDtoListe(sakListe, linkPathBehandling, linkPathUttaksplan);
        LOG.info("Hentet {} statuser", statusListe.size());
        return statusListe;
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
                            .forEach(elem -> {
                                String href = linkPathBehandling + elem.getBehandlingId();
                                dto.leggTilLenke(href, "behandlinger");
                            });

                    repository.hentFagsakRelasjon(sak.getSaksnummer()).ifPresent(up -> {
                        var href = linkPathUttaksplan + sak.getSaksnummer();
                        dto.leggTilLenke(href, "uttaksplan");
                    });
                    return dto;
                }).toList();
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
