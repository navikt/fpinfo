package no.nav.foreldrepenger.info.repository;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;
import no.nav.vedtak.felles.jpa.TomtResultatException;

public interface DokumentForsendelseRepositoryFeil extends DeklarerteFeil {

    DokumentForsendelseRepositoryFeil FACTORY = FeilFactory.create(DokumentForsendelseRepositoryFeil.class);

    @TekniskFeil(feilkode = "FP-741456", feilmelding = "Fant ingen behandling med behandlingId: %s", logLevel = LogLevel.WARN, exceptionClass = TomtResultatException.class)
    Feil fantIkkeBehandlingForBehandlingId(Long behandlingId);

    @TekniskFeil(feilkode = "FP-243483", feilmelding = "Fant ikke søknadsgrunnlag for behandlingId: %s", logLevel = LogLevel.INFO, exceptionClass = TomtResultatException.class)
    Feil fantIkkeSøknadsgrunnlagForBehandling(Long behandlingId);

}
