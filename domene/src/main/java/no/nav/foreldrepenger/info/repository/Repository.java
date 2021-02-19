package no.nav.foreldrepenger.info.repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.FagsakRelasjon;
import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.domene.Sak;
import no.nav.foreldrepenger.info.domene.Saksnummer;
import no.nav.foreldrepenger.info.domene.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.domene.UttakPeriode;
import no.nav.foreldrepenger.info.felles.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.felles.datatyper.FagsakYtelseType;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class Repository {

    private static final Logger LOG = LoggerFactory.getLogger(Repository.class);

    private EntityManager entityManager;

    @Inject
    public Repository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Repository() {
        // CDI
    }

    public List<Sak> hentSak(String aktørId) {
        return getSak(aktørId);
    }

    public List<UttakPeriode> hentUttakPerioder(Long behandlingId) {
        var query = entityManager.createQuery("from UttakPeriode where behandlingId=:behandlingId",
                UttakPeriode.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList();
    }

    public Optional<SøknadsGrunnlag> hentSøknadsGrunnlag(Long behandlingId) {
        var query = entityManager
                .createQuery("from SøknadsGrunnlag where behandlingId=:behandlingId", SøknadsGrunnlag.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList().stream().reduce((first, second) -> second);
    }

    public Optional<Long> hentGjeldendeBehandling(Saksnummer saksnummer) {
        var query = entityManager
                .createNativeQuery("SELECT b.behandling_id from GJELDENDE_VEDTATT_BEHANDLING b where b.saksnummer=?1");
        query.setParameter(1, saksnummer.asString());
        query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);
        var result = query.getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(((BigDecimal) query.getSingleResult()).longValue());
    }

    public Optional<Sak> finnNyesteSakForAnnenPart(String aktørIdBruker, String annenPartAktørId) {
        var query = entityManager.createQuery(
                "from SakStatus where aktørId=:annenPartAktørId and aktørIdAnnenPart=:aktørId and fagsakYtelseType=:ytelseType order by opprettetTidspunkt desc",
                Sak.class);
        query.setParameter("aktørId", aktørIdBruker);
        query.setParameter("ytelseType", FagsakYtelseType.FP.getVerdi());
        query.setParameter("annenPartAktørId", annenPartAktørId);
        return query.getResultList().stream().findFirst();
    }

    public Optional<FagsakRelasjon> hentFagsakRelasjon(String saksnummer) {
        var query = entityManager.createQuery("from FagsakRelasjon where saksnummer=:saksnummer", FagsakRelasjon.class);
        query.setParameter("saksnummer", new TypedParameterValue(StringType.INSTANCE, saksnummer));
        return query.getResultList().stream()
                .max(Comparator.comparing(FagsakRelasjon::getEndretTidspunkt));
    }

    public Behandling hentBehandling(Long behandlingId) {
        var query = entityManager.createQuery("from Behandling where behandling_id=:behandlingId", Behandling.class);
        query.setParameter("behandlingId", behandlingId);
        var resultList = query.getResultList();
        if (resultList.size() > 1) {
            LOG.info("Hent behandling med id {} returnerte {} behandlinger", behandlingId, resultList.size());
        }
        var behandling = resultList.stream().findFirst();

        if (behandling.isEmpty()) {
            throw new TekniskException("FP-741456", String.format("Fant ingen behandling med behandlingId: %s", behandlingId));
        }
        return behandling.get();
    }

    public List<Behandling> hentTilknyttedeBehandlinger(String saksnummer) {
        var query = entityManager.createQuery("from Behandling where saksnummer=:saksnummer", Behandling.class);
        query.setParameter("saksnummer", new TypedParameterValue(StringType.INSTANCE, saksnummer));
        return query.getResultList();
    }

    public List<MottattDokument> hentMottatteDokumenter(UUID forsendelseId) {
        var query = entityManager.createQuery("from MottattDokument where forsendelse_id=:forsendelseId", MottattDokument.class);
        query.setParameter("forsendelseId", forsendelseId);
        return query.getResultList();
    }

    public List<MottattDokument> hentInntektsmeldinger(Long behandlingId) {
        var query = entityManager.createQuery("from MottattDokument where behandling_id=:behandlingId", MottattDokument.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList()
                .stream()
                .filter(o -> DokumentTypeId.INNTEKTSMELDING.getVerdi().equals(o.getType()))
                .collect(Collectors.toList());
    }

    public List<MottattDokument> hentMottattDokument(Long behandlingId) {
        var query = entityManager
                .createQuery("from MottattDokument where behandling_id=:behandlingId", MottattDokument.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList();
    }

    private List<Sak> getSak(String aktørId) {
        var query = entityManager.createQuery("from SakStatus where hoved_soeker_aktoer_id=:aktørId", Sak.class);
        query.setParameter("aktørId", aktørId);
        return query.getResultList();
    }

}
