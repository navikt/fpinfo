package no.nav.foreldrepenger.info.tjenester;

import static no.nav.vedtak.feil.LogLevel.ERROR;
import static no.nav.vedtak.feil.LogLevel.WARN;

import java.util.Set;
import java.util.UUID;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface DokumentforsendelseTjenesteFeil extends DeklarerteFeil {

    DokumentforsendelseTjenesteFeil FACTORY = FeilFactory.create(DokumentforsendelseTjenesteFeil.class);

    @TekniskFeil(feilkode = "FP-760821", feilmelding = "Finner ikke mottatt dokument for forsendelse ID %s", logLevel = WARN)
    Feil finnesIkkeMottatDokument(UUID forsendelseId);

    @TekniskFeil(feilkode = "FP-760822", feilmelding = "Det er flere behandlinger (%s) knyttet til forsendelsen med ID %s", logLevel = ERROR)
    Feil flereBehandlingerForForsendelsen(Set<Long> behandlingsIder, UUID forsendelseId);

    @TekniskFeil(feilkode = "FP-760823", feilmelding = "Ugyldig behandlingsresultat for forsendelse ID %s", logLevel = WARN)
    Feil ugyldigBehandlingResultat(UUID forsendelseId);
}
