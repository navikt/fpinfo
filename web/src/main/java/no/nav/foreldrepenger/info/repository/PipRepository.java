package no.nav.foreldrepenger.info.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;

@Dependent
public class PipRepository {

    private final EntityManager entityManager;

    @Inject
    public PipRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<String> hentAktørIdForBehandling(Set<Long> behandlingIder) {
        Objects.requireNonNull(behandlingIder, "behandlingIder");
        if (behandlingIder.isEmpty()) {
            return List.of();
        }
        return entityManager.createQuery(
                "select s from SakStatus s left join Behandling b on s.saksnummer = b.saksnummer where b.behandlingId in :behandlingIder",
                Sak.class)
                .setParameter("behandlingIder", behandlingIder).getResultList()
                .stream()
                .map(Sak::getAktørId).toList();
    }

    public List<String> hentAktørIdForSaksnummer(Set<String> saksnummre) {
        Objects.requireNonNull(saksnummre, "saksnummre");
        if (saksnummre.isEmpty()) {
            return List.of();
        }
        return entityManager.createQuery("from SakStatus where saksnummer in :saksnummre",
                Sak.class)
                .setParameter("saksnummre", saksnummre).getResultList().stream().map(Sak::getAktørId).toList();
    }

    public Optional<String> hentAnnenPartForSaksnummer(String saksnummer) {
        Objects.requireNonNull(saksnummer, "saksnummer");
        return entityManager.createQuery("from SakStatus where saksnummer like :saksnummer",
                Sak.class)
                .setParameter("saksnummer", saksnummer).getResultList()
                .stream().findFirst()
                .map(Sak::getAktørIdAnnenPart);
    }

    public Optional<Boolean> hentOppgittAleneomsorgForSaksnummer(String saksnummer) {
        Objects.requireNonNull(saksnummer, "saksnummer"); // NOSONAR
        return entityManager
                .createQuery("from SøknadsGrunnlag where saksnummer like :saksnummer", SøknadsGrunnlag.class)
                .setParameter("saksnummer", saksnummer).getResultList()
                .stream()
                .reduce((first, second) -> second)
                .map(SøknadsGrunnlag::getAleneomsorg);
    }

    public Optional<String> finnSakenTilAnnenForelder(Set<String> bruker, Set<String> annenForelder) {
        Objects.requireNonNull(bruker, "bruker");
        Objects.requireNonNull(bruker, "annenForelder");
        if (bruker.size() != 1 || annenForelder.size() != 1) {
            return Optional.empty();
        }

        return entityManager.createQuery(
                """
                  select s from SakStatus s
                        where s.aktørId like :annenForelder and s.aktørIdAnnenPart like :bruker order by s.opprettetTidspunkt desc
                """,
                Sak.class)
                .setParameter("annenForelder", annenForelder.stream().findFirst().orElseThrow())
                .setParameter("bruker", bruker.stream().findFirst().orElseThrow()).getResultList().stream()
                .findFirst()
                .map(Sak::getSaksnummer);
    }
}
