package no.nav.foreldrepenger.info.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import no.nav.foreldrepenger.info.Aksjonspunkt;
import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.FamilieHendelse;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.SøknadsperiodeEntitet;
import no.nav.foreldrepenger.info.UttakPeriode;

public interface Repository {

    List<Sak> hentSak(String aktørId);

    List<UttakPeriode> hentUttakPerioder(Long behandlingId);

    Optional<SøknadsGrunnlag> hentSøknadsGrunnlag(Long behandlingId);

    Optional<Long> hentGjeldendeBehandling(Saksnummer saksnummer);

    List<Behandling> hentTilknyttedeBehandlinger(String saksnummer);

    List<MottattDokument> hentMottattDokument(Long behandlingId);

    Set<String> hentBarn(Saksnummer saksnummer);

    List<SøknadsperiodeEntitet> hentSøknadsperioder(long behandlingId);

    Optional<FamilieHendelse> hentFamilieHendelse(long behandlingId);

    Set<Aksjonspunkt> hentAksjonspunkt(long behandlingId);
}
