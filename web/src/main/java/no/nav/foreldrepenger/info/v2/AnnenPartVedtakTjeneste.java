package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
class AnnenPartVedtakTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(AnnenPartVedtakTjeneste.class);

    private FpSakerTjeneste fpSakerTjeneste;

    @Inject
    public AnnenPartVedtakTjeneste(FpSakerTjeneste fpSakerTjeneste) {
        this.fpSakerTjeneste = fpSakerTjeneste;
    }

    AnnenPartVedtakTjeneste() {
        //CDI
    }

    Optional<AnnenPartVedtak> hentFor(AktørId søkersAktørId,
                                      AktørId annenPartAktørId,
                                      AktørId barn,
                                      LocalDate familiehendelse) {
        var fpSaker = fpSakerTjeneste.hentFor(annenPartAktørId);
        if (fpSaker.isEmpty()) {
            LOG.info("Annen part har ingen saker om foreldrepenger");
            return Optional.empty();
        }

        var gjeldendeSakForAnnenPartOpt = gjeldendeSak(fpSaker, søkersAktørId, barn, familiehendelse);
        if (gjeldendeSakForAnnenPartOpt.isEmpty()) {
            LOG.info("Finner ingen sak som ikke er avsluttet der annen part har oppgitt søker");
            return Optional.empty();
        }
        var gjeldendeSak = gjeldendeSakForAnnenPartOpt.get();
        LOG.info("Fant gjeldende sak for annen part. Saksnummer {}", gjeldendeSak.saksnummer());
        if (gjeldendeSak.gjeldendeVedtak() == null) {
            LOG.info("Annen parts gjeldende sak har ingen gjeldende vedtak. Saksnummer {}", gjeldendeSak.saksnummer());
            return Optional.empty();
        }
        var termindato = gjeldendeSak.familiehendelse().termindato();
        var antallBarn = gjeldendeSak.familiehendelse().antallBarn();
        var dekningsgrad = gjeldendeSak.dekningsgrad();
        return Optional.of(new AnnenPartVedtak(filterSensitive(gjeldendeSak), termindato, dekningsgrad, antallBarn));
    }

    private static List<UttakPeriode> filterSensitive(FpSak gjeldendeSak) {
        //SKal ikke kunne se annen parts arbeidsgivere
        return gjeldendeSak.gjeldendeVedtak().perioder().stream().map(p -> {
            var gradering = p.gradering() == null ? null : new Gradering(p.gradering().arbeidstidprosent(), null);
            return new UttakPeriode(p.fom(), p.tom(),
                    p.kontoType(), p.resultat(), p.utsettelseÅrsak(), p.oppholdÅrsak(), p.overføringÅrsak(), gradering,
                    p.morsAktivitet(), p.samtidigUttak(), p.flerbarnsdager());
        }).toList();
    }

    private Optional<FpSak> gjeldendeSak(Set<FpSak> saker,
                                         AktørId søkersAktørId,
                                         AktørId barn,
                                         LocalDate familiehendelse) {
        var annenPartsSaker = sakerMedAnnenpartLikSøker(søkersAktørId, saker, barn, familiehendelse);

        if (annenPartsSaker.size() > 1) {
            var saksnummer = annenPartsSaker.stream().map(FpSak::saksnummer).collect(Collectors.toSet());
            LOG.warn("Fant flere enn 1 sak ved oppslag av annen parts vedtaksperioder."
                            + " Velger sist opprettet. Saksnummer {}.", saksnummer);
        }
        return annenPartsSaker.stream()
                .max((o1, o2) -> o2.opprettetTidspunkt().compareTo(o1.opprettetTidspunkt()));
    }

    private List<FpSak> sakerMedAnnenpartLikSøker(AktørId søkersAktørId, Set<FpSak> saker, AktørId barn, LocalDate familiehendelse) {
        return saker
                .stream()
                .filter(sak -> sak.annenPart() != null && sak.annenPart().aktørId().equals(søkersAktørId))
                .filter(sak -> barn == null || sak.barn().contains(barn))
                //Sjekker ikke familiehendelse hvis vi har aktørId på barnet
                .filter(sak -> barn != null || familiehendelse == null || sak.familiehendelse().familiehendelse().equals(familiehendelse))
                .toList();
    }
}
