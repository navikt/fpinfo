package no.nav.foreldrepenger.info.v2;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
class AnnenPartsVedtaksperioderTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(AnnenPartsVedtaksperioderTjeneste.class);

    private SakerTjeneste sakerTjeneste;

    @Inject
    public AnnenPartsVedtaksperioderTjeneste(SakerTjeneste sakerTjeneste) {
        this.sakerTjeneste = sakerTjeneste;
    }

    AnnenPartsVedtaksperioderTjeneste() {
        //CDI
    }
    List<VedtakPeriode> hentFor(AktørId søkersAktørId, AktørId annenPartAktørId, AktørId barn) {
        var saker = sakerTjeneste.hentFor(annenPartAktørId);
        if (saker.foreldrepenger().isEmpty()) {
            LOG.info("Annen part har ingen saker om foreldrepenger");
            return List.of();
        }

        var gjeldendeSakForAnnenPartOpt = gjeldendeSak(saker, søkersAktørId, annenPartAktørId, barn);
        if (gjeldendeSakForAnnenPartOpt.isEmpty()) {
            LOG.info("Finner ingen sak som ikke er avsluttet der annen part har oppgitt søker");
            return List.of();
        }
        var gjeldendeSak = gjeldendeSakForAnnenPartOpt.get();
        LOG.info("Fant gjeldende sak for annen part. Saksnummer {}", gjeldendeSak.saksnummer());
        if (gjeldendeSak.gjeldendeVedtak() == null) {
            LOG.info("Annen parts gjeldende sak har ingen gjeldende vedtak. Saksnummer {}", gjeldendeSak.saksnummer());
            return List.of();
        }
        return gjeldendeSak.gjeldendeVedtak().perioder();
    }

    private Optional<FpSak> gjeldendeSak(Saker saker, AktørId søkersAktørId, AktørId annenPartAktørId, AktørId barn) {
        var annenPartsSaker = sakerMedAnnenpartLikSøker(søkersAktørId, saker, barn);

        if (annenPartsSaker.size() > 1) {
            var saksnummer = annenPartsSaker.stream().map(sak -> sak.saksnummer()).collect(Collectors.toSet());
            LOG.warn("Fant fler enn 1 sak ved oppslag av annen parts vedtaksperioder."
                            + " Velger sist opprettet. Søker {} AnnenPart {} Saksnummer {}.",
                    søkersAktørId, annenPartAktørId, saksnummer);
        }
        return annenPartsSaker.stream()
                .max((o1, o2) -> o2.opprettetTidspunkt().compareTo(o1.opprettetTidspunkt()));
    }

    private List<FpSak> sakerMedAnnenpartLikSøker(AktørId søkersAktørId, Saker saker, AktørId barn) {
        return saker.foreldrepenger()
                .stream()
                .filter(sak -> sak.annenPart() != null && sak.annenPart().aktørId().equals(søkersAktørId))
                .filter(sak -> sak.barn().contains(barn))
                .toList();
    }
}
