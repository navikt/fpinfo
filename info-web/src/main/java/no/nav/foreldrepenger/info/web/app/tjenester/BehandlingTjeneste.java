package no.nav.foreldrepenger.info.web.app.tjenester;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.domene.Saksnummer;
import no.nav.foreldrepenger.info.repository.Repository;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.BehandlingIdDto;

@ApplicationScoped
public class BehandlingTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingTjeneste.class);

    private SøknadTjeneste søknadTjeneste;
    private Repository repository;

    @Inject
    public BehandlingTjeneste(SøknadTjeneste søknadTjeneste, Repository repository) {
        this.søknadTjeneste = søknadTjeneste;
        this.repository = repository;
    }

    public BehandlingTjeneste() {
    }

    public BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto, String linkPathSøknad) {
        var behandlingId = behandlingIdDto.getBehandlingId();
        var behandling = repository.hentBehandling(behandlingId);
        var dto = BehandlingDto.fraDomene(behandling);
        if (harSøknad(behandlingIdDto)) {
            dto.leggTilLenke(linkPathSøknad + behandlingId, "søknad");
        }
        var inntektsmeldinger = repository.hentInntektsmeldinger(behandlingId);
        dto.setInntektsmeldinger(journalPostIdr(inntektsmeldinger));
        return dto;
    }

    private List<String> journalPostIdr(List<MottattDokument> inntektsmeldinger) {
        return inntektsmeldinger.stream()
                .map(MottattDokument::getJournalpostId)
                .collect(Collectors.toList());
    }

    private boolean harSøknad(BehandlingIdDto behandlingIdDto) {
        return søknadTjeneste.harSøknad(behandlingIdDto.getBehandlingId());
    }

    public Optional<Long> getGjeldendeBehandlingId(Saksnummer saksnummer) {
        var behandlingId = repository.hentGjeldendeBehandling(saksnummer);
        if (behandlingId.isEmpty()) {
            LOG.info("Finner ingen gjeldende behandlingId for saksnummer " + saksnummer.saksnummer());
        } else {
            LOG.info("Bruker gjeldende behandlingId: " + behandlingId.get());
        }
        return behandlingId;
    }
}
