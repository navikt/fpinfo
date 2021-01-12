package no.nav.foreldrepenger.info.web.app.tjenester;

import static no.nav.vedtak.feil.LogLevel.ERROR;
import static no.nav.vedtak.feil.LogLevel.WARN;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingResultatType;
import no.nav.foreldrepenger.info.felles.datatyper.BehandlingStatus;
import no.nav.foreldrepenger.info.repository.Repository;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.ForsendelseIdDto;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.ForsendelseStatus;
import no.nav.foreldrepenger.info.web.app.tjenester.dto.ForsendelseStatusDto;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

@ApplicationScoped
public class ForsendelseStatusTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(ForsendelseStatusTjeneste.class);

    private Repository repository;

    @Inject
    public ForsendelseStatusTjeneste(Repository repository) {
        this.repository = repository;
    }

    ForsendelseStatusTjeneste() {
        //CDI
    }

    public Optional<ForsendelseStatusDto> hentForsendelseStatus(ForsendelseIdDto forsendelseIdDto) {
        var forsendelseId = forsendelseIdDto.getForsendelseId();

        LOG.info("Henter status for forsendelse {}", forsendelseId);

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
            throw ForsendelseStatusTjenesteFeil.FACTORY.flereBehandlingerForForsendelsen(behandlingsIder, forsendelseId).toException();
        }

        var behandling = repository.hentBehandling(behandlingsIder.iterator().next());
        var forsendelseStatusDataDTO = mapTilDto(behandling, forsendelseId);
        LOG.info("Returnerer behandling {} for forsendelse {}", behandling.getBehandlingId(), forsendelseId);
        return Optional.ofNullable(forsendelseStatusDataDTO);
    }

    private static ForsendelseStatusDto mapTilDto(Behandling behandling, UUID forsendelseId) {
        var behandlingStatus = behandling.getBehandlingStatus();
        if (erAvsluttet(behandlingStatus)) {
            var resultat = behandling.getBehandlingResultatType();
            if (erInnvilget(resultat)) {
                return new ForsendelseStatusDto(ForsendelseStatus.INNVILGET);
            } else if (erAvslått(resultat)) {
                return new ForsendelseStatusDto(ForsendelseStatus.AVSLÅTT);
            } else if (erMergetOgHenlagt(resultat)) {
                // FIXME: finnes ikke funksjonalitet for å håndtere MERGET_OG_HENLAGT
                LOG.info("Behandlingsresultat er {}, fpinfo ser ikke videre på denne",
                        BehandlingResultatType.MERGET_OG_HENLAGT.getVerdi());
                return null;
            } else {
                throw ForsendelseStatusTjenesteFeil.FACTORY.ugyldigBehandlingResultat(forsendelseId).toException();
            }

        }
        var aksjonspunkt = behandling.getÅpneAksjonspunkter();
        if (aksjonspunkt.isEmpty()) {
            return new ForsendelseStatusDto(ForsendelseStatus.PÅGÅR);
        } else {
            return new ForsendelseStatusDto(ForsendelseStatus.PÅ_VENT);
        }
    }

    private static boolean erMergetOgHenlagt(String resultat) {
        return resultat.equals(BehandlingResultatType.MERGET_OG_HENLAGT.getVerdi());
    }

    private static boolean erAvslått(String resultat) {
        return resultat.equals(BehandlingResultatType.AVSLÅTT.getVerdi());
    }

    private static boolean erInnvilget(String resultat) {
        return resultat.equals(BehandlingResultatType.INNVILGET.getVerdi()) || resultat.equals(
                BehandlingResultatType.FORELDREPENGER_ENDRET.getVerdi()) || resultat.equals(
                BehandlingResultatType.INGEN_ENDRING.getVerdi());
    }

    private static boolean erAvsluttet(String behandlingStatus) {
        return behandlingStatus.equals(BehandlingStatus.AVSLUTTET.getVerdi()) || behandlingStatus.equals(
                BehandlingStatus.IVERKSETTER_VEDTAK.getVerdi());
    }

    private interface ForsendelseStatusTjenesteFeil extends DeklarerteFeil {

        ForsendelseStatusTjenesteFeil FACTORY = FeilFactory.create(ForsendelseStatusTjenesteFeil.class);

        @TekniskFeil(feilkode = "FP-760822", feilmelding = "Det er flere behandlinger (%s) knyttet til forsendelsen med ID %s", logLevel = ERROR)
        Feil flereBehandlingerForForsendelsen(Set<Long> behandlingsIder, UUID forsendelseId);

        @TekniskFeil(feilkode = "FP-760823", feilmelding = "Ugyldig behandlingsresultat for forsendelse ID %s", logLevel = WARN)
        Feil ugyldigBehandlingResultat(UUID forsendelseId);
    }
}
