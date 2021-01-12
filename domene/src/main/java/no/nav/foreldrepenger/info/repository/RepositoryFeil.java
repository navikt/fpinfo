package no.nav.foreldrepenger.info.repository;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;
import no.nav.vedtak.felles.jpa.TomtResultatException;

public interface RepositoryFeil extends DeklarerteFeil {

    RepositoryFeil FACTORY = FeilFactory.create(RepositoryFeil.class);

    @TekniskFeil(feilkode = "FP-741456", feilmelding = "Fant ingen behandling med behandlingId: %s", logLevel = LogLevel.WARN, exceptionClass = TomtResultatException.class)
    Feil fantIkkeBehandlingForBehandlingId(Long behandlingId);

}
