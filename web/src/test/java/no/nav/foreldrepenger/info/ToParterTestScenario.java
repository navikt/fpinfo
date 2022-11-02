package no.nav.foreldrepenger.info;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.info.datatyper.BehandlingType;

public class ToParterTestScenario {

    private final InMemTestRepository repository;

    private final String aktørId = UUID.randomUUID().toString();
    private final String aktørIdAnnenpart = UUID.randomUUID().toString();
    private final Saksnummer saksnummer = new Saksnummer(UUID.randomUUID().toString());
    private final Saksnummer saksnummerAnnenpart = new Saksnummer(UUID.randomUUID().toString());

    private final Sak sak = new Sak.Builder()
            .medSaksnummer(saksnummer)
            .medAktørId(aktørId)
            .medAktørIdAnnenPart(aktørIdAnnenpart)
            .build();
    private final Behandling behandling = new Behandling.Builder()
            .medBehandlingId(1L)
            .medSaksnummer(saksnummer)
            .medBehandlingStatus("AVSLU")
            .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
            .build();
    private final Behandling behandlingAnnenpart = new Behandling.Builder()
            .medBehandlingId(2L)
            .medSaksnummer(saksnummerAnnenpart)
            .medBehandlingStatus("AVSLU")
            .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
            .build();

    public ToParterTestScenario(InMemTestRepository repository) {
        this.repository = repository;
        repository.lagre(sak);
        var sakAnnenpart = new Sak.Builder()
                .medSaksnummer(saksnummerAnnenpart)
                .medAktørId(aktørIdAnnenpart)
                .medAktørIdAnnenPart(aktørId)
                .build();
        repository.lagre(sakAnnenpart);
        repository.lagre(behandling);
        repository.lagre(behandlingAnnenpart);
        var fagsakRelasjon = new FagsakRelasjon.Builder()
                .saksnummer(sak.getSaksnummer())
                .saksnummerEn(sak.getSaksnummer())
                .saksnummerTo(sakAnnenpart.getSaksnummer())
                .build();
        repository.lagre(fagsakRelasjon);
    }

    public ToParterTestScenario() {
        this(new InMemTestRepository());
    }

    public InMemTestRepository repository() {
        return repository;
    }

    public ToParterTestScenario uttak(List<UttakPeriode> uttak) {
        repository.lagreVedtaksperioder(behandling.getBehandlingId(), uttak);
        return this;
    }

    public ToParterTestScenario uttakAnnenpart(List<UttakPeriode> uttak) {
        repository.lagreVedtaksperioder(behandlingAnnenpart.getBehandlingId(), uttak);
        return this;
    }

    public Saksnummer saksnummer() {
        return new Saksnummer(sak.getSaksnummer());
    }
}
