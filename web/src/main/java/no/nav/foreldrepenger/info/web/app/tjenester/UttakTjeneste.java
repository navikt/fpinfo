package no.nav.foreldrepenger.info.web.app.tjenester;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.domene.Saksnummer;
import no.nav.foreldrepenger.info.domene.UttakPeriode;
import no.nav.foreldrepenger.info.repository.Repository;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.UttaksPeriodeDto;

@ApplicationScoped
public class UttakTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(UttakTjeneste.class);

    private BehandlingTjeneste behandlingTjeneste;
    private Repository repository;

    @Inject
    public UttakTjeneste(BehandlingTjeneste behandlingTjeneste, Repository repository) {
        this.behandlingTjeneste = behandlingTjeneste;
        this.repository = repository;
    }

    UttakTjeneste() {
        // CDI
    }

    public List<UttaksPeriodeDto> hentFellesUttaksplan(Saksnummer saksnummer, boolean erAnnenPart) {
        LOG.info("Henter felles uttaksplan basert på {}", saksnummer);
        var fagsakRelasjonOptional = repository.hentFagsakRelasjon(saksnummer.saksnummer());
        if (fagsakRelasjonOptional.isEmpty()) {
            LOG.info("Fant ingen uttaksplan for {}", saksnummer);
            return Collections.emptyList();
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
        LOG.info("Returnererer uttaksplan med {} perioder for saksnummer {}", fellesPlan.size(), saksnummer.saksnummer());
        return fellesPlan;
    }

    private List<UttakPeriode> hentUttakPerioder(Saksnummer saksnummer) {
        return behandlingTjeneste.getGjeldendeBehandlingId(saksnummer).map(gb -> repository.hentUttakPerioder(gb))
                .orElse(Collections.emptyList());
    }
}
