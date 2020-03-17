package no.nav.foreldrepenger.info.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.domene.SakStatus;
import no.nav.foreldrepenger.info.domene.Saksnummer;
import no.nav.foreldrepenger.info.domene.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.domene.UttakPeriode;
import no.nav.foreldrepenger.info.domene.FagsakRelasjon;

public interface DokumentForsendelseRepository {
    Behandling hentBehandling(Long behandlingId);

    List<Behandling> hentTilknyttedeBehandlinger(String saksnummer);

    List<MottattDokument> hentMottatteDokumenter(UUID forsendelseId);

    List<MottattDokument> hentInntektsmeldinger(Long behandlingId);

    List<MottattDokument> hentMottattDokument(Long behandlingId);

    List<SakStatus> hentSakStatus(String aktørId);

    Optional<FagsakRelasjon> hentFagsakRelasjon(String saksnummer);

    List<UttakPeriode> hentUttakPerioder(Long behandlingId);

    Optional<SøknadsGrunnlag> hentSøknadsGrunnlag(Long behandlingId);

    Optional<Long> hentGjeldendeBehandling(Saksnummer saksnummer);

    Optional<SakStatus> finnNyesteSakForAnnenPart(String aktørId, String annenPartAktørId);
}
