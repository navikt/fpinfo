package no.nav.foreldrepenger.info.repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        var query = entityManager.createQuery(
                "select s from SakStatus s left join Behandling b on s.saksnummer = b.saksnummer where b.behandlingId in :behandlingIder",
                Sak.class);
        query.setParameter("behandlingIder", behandlingIder);
        return query.getResultList().stream().map(Sak::getAktørId).collect(Collectors.toList());
    }

    public List<String> hentAktørIdForSaksnummer(Set<String> saksnummre) {
        Objects.requireNonNull(saksnummre, "saksnummre");
        if (saksnummre.isEmpty()) {
            return Collections.emptyList();
        }
        var query = entityManager.createQuery("from SakStatus where saksnummer in :saksnummre",
                Sak.class);
        query.setParameter("saksnummre", saksnummre);
        return query.getResultList().stream().map(Sak::getAktørId).collect(Collectors.toList());
    }

    public Optional<String> hentAnnenPartForSaksnummer(String saksnummer) {
        Objects.requireNonNull(saksnummer, "saksnummer");
        var query = entityManager.createQuery("from SakStatus where saksnummer like :saksnummer",
                Sak.class);
        query.setParameter("saksnummer", saksnummer);
        return query.getResultList().stream().findFirst().map(Sak::getAktørIdAnnenPart);
    }

    public Optional<Boolean> hentOppgittAleneomsorgForSaksnummer(String saksnummer) {
        Objects.requireNonNull(saksnummer, "saksnummer"); // NOSONAR
        var query = entityManager
                .createQuery("from SøknadsGrunnlag where saksnummer like :saksnummer", SøknadsGrunnlag.class);
        query.setParameter("saksnummer", saksnummer);
        return query.getResultList().stream().reduce((first, second) -> second).map(SøknadsGrunnlag::getAleneomsorg);
    }

    public List<String> hentAktørIdForForsendelseIder(Set<UUID> dokumentforsendelseIder) {
        Objects.requireNonNull(dokumentforsendelseIder, "dokumentforsendelseIder"); // NOSONAR
        if (dokumentforsendelseIder.isEmpty()) {
            return List.of();
        }

        var query = entityManager.createQuery(
                """
                select s from SakStatus s
                        left join MottattDokument m on s.saksnummer = m.saksnummer
                        where m.forsendelseId in :dokumentforsendelseIder
                """,
                Sak.class);
        query.setParameter("dokumentforsendelseIder", dokumentforsendelseIder);

        return query.getResultList().stream().map(Sak::getAktørId).collect(Collectors.toList());
    }

    public Optional<String> finnSakenTilAnnenForelder(Set<String> bruker, Set<String> annenForelder) {
        Objects.requireNonNull(bruker, "bruker");
        Objects.requireNonNull(bruker, "annenForelder");
        if ((bruker.size() != 1) || (annenForelder.size() != 1)) {
            return Optional.empty();
        }

        var query = entityManager.createQuery(
                """
                  select s from SakStatus s
                        where s.aktørId like :annenForelder and s.aktørIdAnnenPart like :bruker order by s.opprettetTidspunkt desc
                """,
                Sak.class);
        query.setParameter("annenForelder", annenForelder.stream().findFirst().get());
        query.setParameter("bruker", bruker.stream().findFirst().get());

        return query.getResultList().stream()
                .findFirst()
                .map(Sak::getSaksnummer);
    }
}
