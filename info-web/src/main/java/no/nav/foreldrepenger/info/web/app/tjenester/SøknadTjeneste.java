package no.nav.foreldrepenger.info.web.app.tjenester;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.repository.Repository;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.SøknadXmlDto;

@ApplicationScoped
public class SøknadTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(SøknadTjeneste.class);
    private Repository repository;

    @Inject
    public SøknadTjeneste(Repository repository) {
        this.repository = repository;
    }

    public SøknadTjeneste() {
        // CDI
    }

    public Optional<SøknadXmlDto> hentSøknadXml(Long behandlingId) {
        LOG.info("henter søknad for behandling {}", behandlingId);
        var mottatteDokumenter = repository.hentMottattDokument(behandlingId)
                .stream()
                .filter(MottattDokument::erSøknad)
                .collect(Collectors.toList());
        return Optional.of(mottatteDokumenter).flatMap(this::mapTilSøknadXml);
    }

    public boolean harSøknad(Long behandlingId) {
        return hentSøknadXml(behandlingId).isPresent();
    }

    private Optional<SøknadXmlDto> mapTilSøknadXml(List<MottattDokument> dokumenter) {
        // Noen søknader er lagret i to innslag hvor ett innslag har XML payload og det
        // andre har journalpostId
        if (dokumenter.size() == 2) {
            return Optional.of(SøknadXmlDto.fraDomene(dokumenter.get(0), dokumenter.get(1)));
        }
        return dokumenter.stream()
                .findFirst()
                .map(SøknadXmlDto::fraDomene);
    }
}
