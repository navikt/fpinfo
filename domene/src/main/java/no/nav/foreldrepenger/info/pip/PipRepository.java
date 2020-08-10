package no.nav.foreldrepenger.info.pip;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import no.nav.foreldrepenger.info.domene.SakStatus;
import no.nav.foreldrepenger.info.domene.SøknadsGrunnlag;

@ApplicationScoped
public class PipRepository {

    private EntityManager entityManager;

    public PipRepository() {
        // CDI
    }

    @Inject
    public PipRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<String> hentAktørIdForBehandling(Set<Long> behandlingIder) {
        Objects.requireNonNull(behandlingIder, "behandlingIder"); // NOSONAR
        if (behandlingIder.isEmpty()) {
            return Collections.emptyList();
        }
        TypedQuery<SakStatus> query = entityManager.createQuery(
                "select s from SakStatus s left join Behandling b on s.saksnummer = b.saksnummer where b.behandlingId in :behandlingIder",
                SakStatus.class);
        query.setParameter("behandlingIder", behandlingIder);
        return query.getResultList().stream().map(SakStatus::getAktørId).collect(Collectors.toList());
    }

    public List<String> hentAktørIdForSaksnummer(Set<String> saksnummre) {
        Objects.requireNonNull(saksnummre, "saksnummre"); // NOSONAR
        if (saksnummre.isEmpty()) {
            return Collections.emptyList();
        }
        TypedQuery<SakStatus> query = entityManager.createQuery("from SakStatus where saksnummer in :saksnummre",
                SakStatus.class);
        query.setParameter("saksnummre", saksnummre);
        return query.getResultList().stream().map(SakStatus::getAktørId).collect(Collectors.toList());
    }

    public Optional<String> hentAnnenPartForSaksnummer(String saksnummer) {
        Objects.requireNonNull(saksnummer, "saksnummer"); // NOSONAR
        TypedQuery<SakStatus> query = entityManager.createQuery("from SakStatus where saksnummer like :saksnummer",
                SakStatus.class);
        query.setParameter("saksnummer", saksnummer);
        return query.getResultList().stream().findFirst().map(SakStatus::getAktørIdAnnenPart);
    }

    public Optional<Boolean> erAleneomsorg(String saksnummer) {
        Objects.requireNonNull(saksnummer, "saksnummer"); // NOSONAR
        TypedQuery<SøknadsGrunnlag> query = entityManager
                .createQuery("from SøknadsGrunnlag where saksnummer like :saksnummer", SøknadsGrunnlag.class);
        query.setParameter("saksnummer", saksnummer);
        return query.getResultList().stream().reduce((first, second) -> second).map(SøknadsGrunnlag::getAleneomsorg);
    }

    public List<String> hentAktørIdForForsendelseIder(Set<UUID> dokumentforsendelseIder) {
        Objects.requireNonNull(dokumentforsendelseIder, "dokumentforsendelseIder"); // NOSONAR
        if (dokumentforsendelseIder.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<SakStatus> query = entityManager.createQuery(
                "select s from SakStatus s " +
                        "left join MottattDokument m on s.saksnummer = m.saksnummer " +
                        "where m.forsendelseId in :dokumentforsendelseIder",
                SakStatus.class);
        query.setParameter("dokumentforsendelseIder", dokumentforsendelseIder);

        return query.getResultList().stream().map(SakStatus::getAktørId).collect(Collectors.toList());
    }

    public Optional<String> finnSaksnummerTilAnnenpart(Set<String> bruker, Set<String> annenpart) {
        Objects.requireNonNull(bruker, "bruker");
        Objects.requireNonNull(bruker, "annenpart");
        if ((bruker.size() != 1) || (annenpart.size() != 1)) {
            return Optional.empty();
        }

        TypedQuery<SakStatus> query = entityManager.createQuery(
                "select s from SakStatus s " +
                        "where s.aktørId like :annenpart and s.aktørIdAnnenPart like :bruker order by s.opprettetTidspunkt desc",
                SakStatus.class);
        query.setParameter("annenpart", annenpart.stream().findFirst().get());// NOSONAR
        query.setParameter("bruker", bruker.stream().findFirst().get());// NOSONAR

        return query.getResultList().stream().findFirst().map(SakStatus::getSaksnummer);
    }
}
