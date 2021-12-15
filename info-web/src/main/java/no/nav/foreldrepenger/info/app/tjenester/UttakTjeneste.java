package no.nav.foreldrepenger.info.app.tjenester;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.UttakPeriode;
import no.nav.foreldrepenger.info.app.tjenester.dto.UttaksPeriodeDto;
import no.nav.foreldrepenger.info.repository.Repository;

@Dependent
public class UttakTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(UttakTjeneste.class);

    private final BehandlingTjeneste behandlingTjeneste;
    private final Repository repository;

    @Inject
    public UttakTjeneste(BehandlingTjeneste behandlingTjeneste, Repository repository) {
        this.behandlingTjeneste = behandlingTjeneste;
        this.repository = repository;
    }

    public List<UttaksPeriodeDto> hentFellesUttaksplan(Saksnummer saksnummer, boolean erAnnenPart) {
        LOG.trace("Henter felles uttaksplan basert på {}", saksnummer);
        var fagsakRelasjonOptional = repository.hentFagsakRelasjon(saksnummer.saksnummer());
        if (fagsakRelasjonOptional.isEmpty()) {
            LOG.info("Fant ingen uttaksplan for {}", saksnummer);
            return List.of();
        }

        var fagsakRelasjon = fagsakRelasjonOptional.get();
        var fellesPlan = hentUttakPerioder(fagsakRelasjon.getSaksnummer()).stream()
                .map(up -> UttaksPeriodeDto.fraDomene(saksnummer, up, erAnnenPart))
                .collect(Collectors.toList());

        var annenPartFagsak = fagsakRelasjon.finnSaksnummerAnnenpart();
        if (annenPartFagsak.isPresent()) {
            var uttaksperioderAnnenpart = hentUttakPerioder(annenPartFagsak.get());
            for (var up : uttaksperioderAnnenpart) {
                var uttaksPeriodeDto = UttaksPeriodeDto.fraDomene(saksnummer, up, !erAnnenPart);
                fellesPlan.add(uttaksPeriodeDto);
            }
        }
        LOG.info("Hentet uttaksplan med {} perioder for saksnummer {}", fellesPlan.size(), saksnummer.saksnummer());
        return fellesPlan;
    }

    private List<UttakPeriode> hentUttakPerioder(Saksnummer saksnummer) {
        return behandlingTjeneste.getGjeldendeBehandlingId(saksnummer).map(repository::hentUttakPerioder)
                .orElse(List.of());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [behandlingTjeneste=" + behandlingTjeneste + ", repository=" + repository + "]";
    }
}
