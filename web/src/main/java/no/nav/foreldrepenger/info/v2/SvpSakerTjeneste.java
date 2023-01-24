package no.nav.foreldrepenger.info.v2;

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
                .map(s -> new SvpSakRef(new no.nav.foreldrepenger.info.v2.Saksnummer(s.getSaksnummer()), s.getFagsakStatus()))
                //Får en sak per behandling fra repo
                .distinct()
                .flatMap(s -> hentSvpSak(s).stream())
                .collect(Collectors.toSet());
    }

    private Optional<SvpSak> hentSvpSak(SvpSakRef svpSak) {
        var åpenBehandling = finnÅpenBehandling(svpSak.saksnummer);

        var behandlingIdOpt = åpenBehandling.map(åb -> {
                    LOG.info("Fant åpen behandling {} for sak {}", åb.getBehandlingId(), svpSak.saksnummer);
                    return Optional.of(åb.getBehandlingId());
                })
                .orElseGet(() -> {
                    LOG.info("Fant ingen åpen behandling på sak {}. Henter gjeldende behandling", svpSak.saksnummer);
                    return repository.hentGjeldendeBehandling(new Saksnummer(svpSak.saksnummer.value()));
                });
        if (behandlingIdOpt.isEmpty()) {
            //Henleggelser
            LOG.info("Sak uten åpen behandling eller vedtak {}", svpSak.saksnummer);
            return Optional.empty();
        }
        var behandlingId = behandlingIdOpt.get();
        LOG.info("Henter sak med behandling id {} sak {}", behandlingId, svpSak);
        var fh = repository.hentFamilieHendelse(behandlingId).orElseThrow();
        if (fh.getOmsorgsovertakelseDato() != null) {
            LOG.warn("Forventer ikke svp sak med omsorgsovertakelse. Returnerer null." +
                    " Sak {} Behandling {}", svpSak.saksnummer, behandlingId);
        }
        var familiehendelse = new Familiehendelse(fh.getFødselsdato(), fh.getTermindato(), fh.getAntallBarn(), null);
        var sakAvsluttet = Objects.equals(svpSak.fagsakStatus(), "AVSLU");
        return Optional.of(new SvpSak(svpSak.saksnummer, familiehendelse, sakAvsluttet, åpenBehandling.map(b -> map(b)).orElse(null)));
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

    private SvpÅpenBehandling map(Behandling behandling) {
        //TODO
        return new SvpÅpenBehandling(BehandlingTilstand.UNDER_BEHANDLING);
    }

    private record SvpSakRef(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer,
                             String fagsakStatus) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var svpSakRef = (SvpSakRef) o;
            return saksnummer.equals(svpSakRef.saksnummer);
        }

        @Override
        public int hashCode() {
            return Objects.hash(saksnummer);
        }

        @Override
        public String toString() {
            return "SvpSakRef{" + "saksnummer=" + saksnummer + ", fagsakStatus='" + fagsakStatus + '\'' + '}';
        }
    }
}
