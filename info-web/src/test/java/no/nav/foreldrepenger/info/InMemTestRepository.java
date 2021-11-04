package no.nav.foreldrepenger.info;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.info.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.repository.Repository;

public class InMemTestRepository implements Repository {

    private final List<Sak> saker = new ArrayList<>();
    private final Map<Long, List<UttakPeriode>> uttak = new HashMap<>();
    private final Map<Long, SøknadsGrunnlag> søknadsGrunnlag = new HashMap<>();
    private final List<Behandling> behandlinger = new ArrayList<>();
    private final List<FagsakRelasjon> fagsakRelasjoner = new ArrayList<>();
    private final List<MottattDokument> mottattDokumenter = new ArrayList<>();

    @Override
    public List<Sak> hentSak(String aktørId) {
        return saker.stream().filter(s -> Objects.equals(s.getAktørId(), aktørId)).toList();
    }

    @Override
    public List<UttakPeriode> hentUttakPerioder(Long behandlingId) {
        return List.copyOf(uttak.get(behandlingId));
    }

    @Override
    public Optional<SøknadsGrunnlag> hentSøknadsGrunnlag(Long behandlingId) {
        if (søknadsGrunnlag.containsKey(behandlingId)) {
            return Optional.of(søknadsGrunnlag.get(behandlingId));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> hentGjeldendeBehandling(Saksnummer saksnummer) {
        return behandlinger.stream()
                .filter(behandling -> behandling.getSaksnummer().equals(saksnummer.saksnummer()))
                .max(Comparator.comparing(Behandling::getOpprettetTidspunkt))
                .map(Behandling::getBehandlingId);
    }

    @Override
    public Optional<Sak> finnNyesteSakForAnnenPart(String aktørIdBruker, String annenPartAktørId) {
        return saker.stream()
                .filter(sak -> sak.getAktørId().equals(annenPartAktørId) && sak.getAktørIdAnnenPart().equals(aktørIdBruker))
                .max(Comparator.comparing(Sak::getOpprettetTidspunkt));
    }

    @Override
    public Optional<FagsakRelasjon> hentFagsakRelasjon(String saksnummer) {
        return fagsakRelasjoner.stream().filter(fr -> Objects.equals(fr.getSaksnummer().saksnummer(), saksnummer)).findFirst();
    }

    @Override
    public Behandling hentBehandling(Long behandlingId) {
        return behandlinger.stream().filter(b -> b.getBehandlingId().equals(behandlingId)).findFirst().orElseThrow();
    }

    @Override
    public List<Behandling> hentTilknyttedeBehandlinger(String saksnummer) {
        return behandlinger.stream().filter(b -> b.getSaksnummer().equals(saksnummer)).toList();
    }

    @Override
    public List<MottattDokument> hentMottatteDokumenter(UUID forsendelseId) {
        return mottattDokumenter.stream()
                .filter(md -> Objects.equals(md.getForsendelseId(), forsendelseId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MottattDokument> hentInntektsmeldinger(Long behandlingId) {
        return hentMottattDokument(behandlingId).stream()
                .filter(md -> md.getType().equals(DokumentTypeId.INNTEKTSMELDING.name()))
                .toList();
    }

    @Override
    public List<MottattDokument> hentMottattDokument(Long behandlingId) {
        return mottattDokumenter.stream()
                .filter(md -> Objects.equals(md.getBehandlingId(), behandlingId))
                .collect(Collectors.toList());
    }

    public void lagre(Sak sak) {
        saker.add(sak);
    }

    public void lagre(Behandling behandling) {
        behandlinger.add(behandling);
    }

    public void lagre(Long behandlingId, List<UttakPeriode> uttak) {
        this.uttak.put(behandlingId, List.copyOf(uttak));
    }

    public void lagre(FagsakRelasjon fagsakRelasjon) {
        fagsakRelasjoner.add(fagsakRelasjon);
    }

    public void lagre(List<MottattDokument> dokumenter) {
        mottattDokumenter.addAll(dokumenter);
    }
}