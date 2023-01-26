package no.nav.foreldrepenger.info.v2;

import static no.nav.foreldrepenger.info.v2.SakerFelles.finnBehandlingTilstand;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.repository.Repository;


@ApplicationScoped
class EsSakerTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(EsSakerTjeneste.class);

    private Repository repository;

    @Inject
    public EsSakerTjeneste(Repository repository) {
        this.repository = repository;
    }

    EsSakerTjeneste() {
        //CDI
    }

    public Set<EsSak> hentFor(AktørId aktørId) {
        var sakList = repository.hentSak(aktørId.value());
        return sakList.stream()
                .filter(s -> s.getFagsakYtelseType().equals(FagsakYtelseType.ES.name()))
                .map(s -> new SakRef(new no.nav.foreldrepenger.info.v2.Saksnummer(s.getSaksnummer()), s.getFagsakStatus()))
                //Får en sak per behandling fra repo
                .distinct()
                .flatMap(s -> hentEsSak(s).stream())
                .collect(Collectors.toSet());
    }

    private Optional<EsSak> hentEsSak(SakRef esSak) {
        var åpenBehandling = finnÅpenBehandling(esSak.saksnummer());

        var behandlingIdOpt = åpenBehandling.map(åb -> {
                    LOG.info("Fant åpen behandling {} for sak {}", åb.getBehandlingId(), esSak.saksnummer());
                    return Optional.of(åb.getBehandlingId());
                })
                .orElseGet(() -> {
                    LOG.info("Fant ingen åpen behandling på sak {}. Henter gjeldende behandling", esSak.saksnummer());
                    return repository.hentGjeldendeBehandling(new no.nav.foreldrepenger.info.Saksnummer(esSak.saksnummer().value()));
                });
        if (behandlingIdOpt.isEmpty()) {
            //Henleggelser
            LOG.info("Sak uten åpen behandling eller vedtak {}", esSak.saksnummer());
            return Optional.empty();
        }
        var behandlingId = behandlingIdOpt.get();
        LOG.info("Henter sak med behandling id {} sak {}", behandlingId, esSak);
        var fh = repository.hentFamilieHendelse(behandlingId).orElseThrow();
        var familiehendelse = new Familiehendelse(fh.getFødselsdato(), fh.getTermindato(), fh.getAntallBarn(), fh.getOmsorgsovertakelseDato());
        var sakAvsluttet = Objects.equals(esSak.fagsakStatus(), "AVSLU");
        return Optional.of(new EsSak(esSak.saksnummer(), familiehendelse, sakAvsluttet, åpenBehandling.map(b -> map(b)).orElse(null)));
    }

    private Optional<Behandling> finnÅpenBehandling(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer) {
        LOG.info("Henter åpen behandling for sak {}", saksnummer);
        return repository.hentTilknyttedeBehandlinger(saksnummer.value())
                .stream()
                .filter(b -> !b.erAvsluttet())
                .filter(b -> erRelevant(b))
                .max(Comparator.comparing(Behandling::getOpprettetTidspunkt));
    }

    private boolean erRelevant(Behandling behandling) {
        var dokumenter = repository.hentMottattDokument(behandling.getBehandlingId());
        return dokumenter.stream().anyMatch(MottattDokument::erSøknad);
    }

    private EsÅpenBehandling map(Behandling behandling) {
        var aksjonspunkter = repository.hentAksjonspunkt(behandling.getBehandlingId());
        var tilstand = finnBehandlingTilstand(aksjonspunkter);
        return new EsÅpenBehandling(tilstand);
    }
}
