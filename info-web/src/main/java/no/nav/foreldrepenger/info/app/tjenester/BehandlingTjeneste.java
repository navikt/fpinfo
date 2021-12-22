package no.nav.foreldrepenger.info.app.tjenester;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.app.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.BehandlingIdDto;
import no.nav.foreldrepenger.info.repository.Repository;

@ApplicationScoped
public class BehandlingTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(BehandlingTjeneste.class);

    private Repository repository;

    @Inject
    public BehandlingTjeneste(Repository repository) {
        this.repository = repository;
    }

    BehandlingTjeneste() {
        //CDI
    }

    public BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto) {
        var behandlingId = behandlingIdDto.getBehandlingId();
        var behandling = repository.hentBehandling(behandlingId);
        var dto = BehandlingDto.fraDomene(behandling);
        var inntektsmeldinger = repository.hentInntektsmeldinger(behandlingId);
        dto.setInntektsmeldinger(journalPostIdr(inntektsmeldinger));
        return dto;
    }

    private List<String> journalPostIdr(List<MottattDokument> inntektsmeldinger) {
        return inntektsmeldinger.stream()
                .map(MottattDokument::getJournalpostId)
                .toList();
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
