package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
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

    List<VedtakPeriode> hentFor(AktørId søkersAktørId,
                                @Nullable AktørId annenPartAktørId,
                                @Nullable AktørId barn,
                                @Nullable LocalDate familiehendelse) {
        var fpSaker = sakerTjeneste.hentFor(annenPartAktørId);
        if (fpSaker.isEmpty()) {
            LOG.info("Annen part har ingen saker om foreldrepenger");
            return List.of();
        }

        var gjeldendeSakForAnnenPartOpt = gjeldendeSak(fpSaker, søkersAktørId, annenPartAktørId, barn);
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

    private Optional<FpSak> gjeldendeSak(Set<FpSak> saker, AktørId søkersAktørId, AktørId annenPartAktørId, AktørId barn) {
        var annenPartsSaker = sakerMedAnnenpartLikSøker(søkersAktørId, saker, barn);

        if (annenPartsSaker.size() > 1 && barn != null) {
            var saksnummer = annenPartsSaker.stream().map(FpSak::saksnummer).collect(Collectors.toSet());
            LOG.warn("Fant flere enn 1 sak ved oppslag av annen parts vedtaksperioder."
                            + " Velger sist opprettet. Søker {} AnnenPart {} Saksnummer {}.",
                    søkersAktørId, annenPartAktørId, saksnummer);
        }
        return annenPartsSaker.stream()
                .max((o1, o2) -> o2.opprettetTidspunkt().compareTo(o1.opprettetTidspunkt()));
    }

    private List<FpSak> sakerMedAnnenpartLikSøker(AktørId søkersAktørId, Set<FpSak> saker, AktørId barn) {
        return saker
                .stream()
                .filter(sak -> sak.annenPart() != null && sak.annenPart().aktørId().equals(søkersAktørId))
                .filter(sak -> sak.barn().contains(barn))
                .toList();
    }
}
