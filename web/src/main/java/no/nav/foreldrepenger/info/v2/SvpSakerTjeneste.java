package no.nav.foreldrepenger.info.v2;

import static no.nav.foreldrepenger.info.v2.BehandlingTilstandUtleder.utled;

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
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.datatyper.FagsakYtelseType;
import no.nav.foreldrepenger.info.repository.Repository;


@ApplicationScoped
class SvpSakerTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(SvpSakerTjeneste.class);

    private Repository repository;

    @Inject
    public SvpSakerTjeneste(Repository repository) {
        this.repository = repository;
    }

    SvpSakerTjeneste() {
        //CDI
    }

    public Set<SvpSak> hentFor(AktørId aktørId) {
        var sakList = repository.hentSak(aktørId.value());
        return sakList.stream()
                .filter(s -> s.getFagsakYtelseType().equals(FagsakYtelseType.SVP.name()))
                .map(s -> new SakRef(new no.nav.foreldrepenger.info.v2.Saksnummer(s.getSaksnummer()), s.getFagsakStatus()))
                //Får en sak per behandling fra repo
                .distinct()
                .flatMap(s -> hentSvpSak(s).stream())
                .collect(Collectors.toSet());
    }

    private Optional<SvpSak> hentSvpSak(SakRef svpSak) {
        var åpenBehandling = finnÅpenBehandling(svpSak.saksnummer());

        var behandlingIdOpt = åpenBehandling.map(åb -> {
                    LOG.info("Fant åpen behandling {} for sak {}", åb.getBehandlingId(), svpSak.saksnummer());
                    return Optional.of(åb.getBehandlingId());
                })
                .orElseGet(() -> {
                    LOG.info("Fant ingen åpen behandling på sak {}. Henter gjeldende behandling", svpSak.saksnummer());
                    return repository.hentGjeldendeBehandling(new Saksnummer(svpSak.saksnummer().value()));
                });
        if (behandlingIdOpt.isEmpty()) {
            //Henleggelser
            LOG.info("Sak uten åpen behandling eller vedtak {}", svpSak.saksnummer());
            return Optional.empty();
        }
        var behandlingId = behandlingIdOpt.get();
        LOG.info("Henter sak med behandling id {} sak {}", behandlingId, svpSak);
        var familiehendelse = familiehendelse(svpSak, behandlingId);
        var sakAvsluttet = Objects.equals(svpSak.fagsakStatus(), "AVSLU");
        return Optional.of(new SvpSak(svpSak.saksnummer(), familiehendelse.orElse(null), sakAvsluttet, åpenBehandling.map(this::map).orElse(null)));
    }

    private Optional<Familiehendelse> familiehendelse(SakRef svpSak, Long behandlingId) {
        var fhOpt = repository.hentFamilieHendelse(behandlingId);
        if (fhOpt.isEmpty()) {
            LOG.info("Sak uten familiehendelse {} {}", svpSak.saksnummer(), behandlingId);
            return Optional.empty();
        }
        var fh = fhOpt.get();
        if (fh.getOmsorgsovertakelseDato() != null) {
            LOG.warn("Forventer ikke svp sak med omsorgsovertakelse. Returnerer null." +
                " Sak {} Behandling {}", svpSak.saksnummer(), behandlingId);
        }
        return Optional.of(new Familiehendelse(fh.getFødselsdato(), fh.getTermindato(), fh.getAntallBarn(), null));
    }

    private Optional<Behandling> finnÅpenBehandling(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer) {
        LOG.info("Henter åpen behandling for sak {}", saksnummer);
        var behandlinger = repository.hentTilknyttedeBehandlinger(saksnummer.value());
        return behandlinger
                .stream()
                .filter(b -> !b.erAvsluttet())
                .filter(this::erRelevant)
                .max(Comparator.comparing(Behandling::getOpprettetTidspunkt));
    }

    private boolean erRelevant(Behandling behandling) {
        var dokumenter = repository.hentMottattDokument(behandling.getBehandlingId());
        return dokumenter.stream().anyMatch(MottattDokument::erSøknad);
    }

    private SvpÅpenBehandling map(Behandling behandling) {
        var aksjonspunkter = repository.hentAksjonspunkt(behandling.getBehandlingId());
        var tilstand = utled(aksjonspunkter);
        return new SvpÅpenBehandling(tilstand);
    }
}
