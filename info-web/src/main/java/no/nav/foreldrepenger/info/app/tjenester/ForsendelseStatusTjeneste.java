package no.nav.foreldrepenger.info.app.tjenester;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.app.tjenester.dto.ForsendelseIdDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.ForsendelseStatus;
import no.nav.foreldrepenger.info.app.tjenester.dto.ForsendelseStatusDto;
import no.nav.foreldrepenger.info.datatyper.BehandlingResultatType;
import no.nav.foreldrepenger.info.datatyper.BehandlingStatus;
import no.nav.foreldrepenger.info.repository.Repository;
import no.nav.vedtak.exception.TekniskException;

@Dependent
public class ForsendelseStatusTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(ForsendelseStatusTjeneste.class);

    private final Repository repository;

    @Inject
    public ForsendelseStatusTjeneste(Repository repository) {
        this.repository = repository;
    }

    public Optional<ForsendelseStatusDto> hentForsendelseStatus(ForsendelseIdDto forsendelseIdDto) {
        var forsendelseId = forsendelseIdDto.forsendelseId();

        LOG.trace("Henter status for forsendelse {}", forsendelseId);

        var mottatteDokumenter = repository.hentMottatteDokumenter(forsendelseId);
        if (mottatteDokumenter.isEmpty()) {
            LOG.info("Fant ingen dokumenter for forsendelse {}", forsendelseId);
            return Optional.empty();
        }
        var behandlingsIder = mottatteDokumenter.stream()
                .map(MottattDokument::getBehandlingId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (behandlingsIder.isEmpty()) {
            LOG.info("Returnerer MOTTATT for forsendelse {}", forsendelseId);
            return Optional.of(new ForsendelseStatusDto(ForsendelseStatus.MOTTATT));
        }
        if (behandlingsIder.size() > 1) {
            throw ForsendelseStatusTjenesteFeil.flereBehandlingerForForsendelsen(behandlingsIder, forsendelseId);
        }

        var behandling = repository.hentBehandling(behandlingsIder.iterator().next());
        var forsendelseStatusDataDTO = mapTilDto(behandling, forsendelseId);
        LOG.info("Hentet behandling {} for forsendelse {}", behandling.getBehandlingId(), forsendelseId);
        return Optional.ofNullable(forsendelseStatusDataDTO);
    }

    private static ForsendelseStatusDto mapTilDto(Behandling behandling, UUID forsendelseId) {
        var behandlingStatus = behandling.getBehandlingStatus();
        if (erAvsluttet(behandlingStatus)) {
            var resultat = behandling.getBehandlingResultatType();
            if (erInnvilget(resultat)) {
                return new ForsendelseStatusDto(ForsendelseStatus.INNVILGET);
            }
            if (erAvslått(resultat)) {
                return new ForsendelseStatusDto(ForsendelseStatus.AVSLÅTT);
            }
            if (erMergetOgHenlagt(resultat)) {
                // FIXME: finnes ikke funksjonalitet for å håndtere MERGET_OG_HENLAGT
                LOG.info("Behandlingsresultat er {}, fpinfo ser ikke videre på denne",
                        BehandlingResultatType.MERGET_OG_HENLAGT.name());
                return null;
            }
            throw ForsendelseStatusTjenesteFeil.ugyldigBehandlingResultat(forsendelseId);
        }
        var aksjonspunkt = behandling.getÅpneAksjonspunkter();
        if (aksjonspunkt.isEmpty()) {
            return new ForsendelseStatusDto(ForsendelseStatus.PÅGÅR);
        } else {
            return new ForsendelseStatusDto(ForsendelseStatus.PÅ_VENT);
        }
    }

    private static boolean erMergetOgHenlagt(String resultat) {
        return resultat.equals(BehandlingResultatType.MERGET_OG_HENLAGT.name());
    }

    private static boolean erAvslått(String resultat) {
        return resultat.equals(BehandlingResultatType.AVSLÅTT.name());
    }

    private static boolean erInnvilget(String resultat) {
        return resultat.equals(BehandlingResultatType.INNVILGET.name()) || resultat.equals(
                BehandlingResultatType.FORELDREPENGER_ENDRET.name())
                || resultat.equals(
                        BehandlingResultatType.INGEN_ENDRING.name());
    }

    private static boolean erAvsluttet(String behandlingStatus) {
        return behandlingStatus.equals(BehandlingStatus.AVSLUTTET.getVerdi()) || behandlingStatus.equals(
                BehandlingStatus.IVERKSETTER_VEDTAK.getVerdi());
    }

    private static class ForsendelseStatusTjenesteFeil {

        static TekniskException flereBehandlingerForForsendelsen(Set<Long> behandlingsIder, UUID forsendelseId) {
            return new TekniskException("FP-760822",
                    String.format("Det er flere behandlinger (%s) knyttet til forsendelsen med ID %s", behandlingsIder, forsendelseId));
        }

        static TekniskException ugyldigBehandlingResultat(UUID forsendelseId) {
            return new TekniskException("FP-760822", String.format("Ugyldig behandlingsresultat for forsendelse ID %s", forsendelseId));
        }
    }
}
