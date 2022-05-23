package no.nav.foreldrepenger.info.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.FagsakRelasjon;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.UttakPeriode;

public interface Repository {

    List<Sak> hentSak(String aktørId);

    List<UttakPeriode> hentUttakPerioder(Long behandlingId);

    Optional<SøknadsGrunnlag> hentSøknadsGrunnlag(Long behandlingId);

    Optional<Long> hentGjeldendeBehandling(Saksnummer saksnummer);

    Optional<Sak> finnNyesteSakForAnnenPart(String aktørIdBruker, String annenPartAktørId);

    Optional<FagsakRelasjon> hentFagsakRelasjon(String saksnummer);

    Behandling hentBehandling(Long behandlingId);

    List<Behandling> hentTilknyttedeBehandlinger(String saksnummer);

    List<MottattDokument> hentInntektsmeldinger(Long behandlingId);

    List<MottattDokument> hentMottattDokument(Long behandlingId);

    Set<String> hentBarn(Saksnummer saksnummer);
}