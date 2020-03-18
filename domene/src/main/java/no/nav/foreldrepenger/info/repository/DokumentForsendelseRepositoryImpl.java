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
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import no.nav.foreldrepenger.info.domene.Behandling;
import no.nav.foreldrepenger.info.domene.MottattDokument;
import no.nav.foreldrepenger.info.domene.SakStatus;
import no.nav.foreldrepenger.info.domene.Saksnummer;
import no.nav.foreldrepenger.info.domene.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.domene.UttakPeriode;
import no.nav.foreldrepenger.info.domene.FagsakRelasjon;
import no.nav.foreldrepenger.info.felles.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.felles.datatyper.FagsakYtelseType;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;
import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StringType;

@ApplicationScoped
public class DokumentForsendelseRepositoryImpl implements DokumentForsendelseRepository {

    private EntityManager entityManager;

    public DokumentForsendelseRepositoryImpl() {
        // CDI
    }

    @Inject
    public DokumentForsendelseRepositoryImpl(@VLPersistenceUnit EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<SakStatus> hentSakStatus(String aktørId) {
        return getSakStatus(aktørId);
    }

    @Override
    public List<UttakPeriode> hentUttakPerioder(Long behandlingId) {
        TypedQuery<UttakPeriode> query = entityManager.createQuery("from UttakPeriode where behandlingId=:behandlingId", UttakPeriode.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList();
    }

    @Override
    public Optional<SøknadsGrunnlag> hentSøknadsGrunnlag(Long behandlingId) {
        TypedQuery<SøknadsGrunnlag> query = entityManager.createQuery("from SøknadsGrunnlag where behandlingId=:behandlingId", SøknadsGrunnlag.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList().stream().reduce((first, second) -> second);
    }

    @Override
    public Optional<Long> hentGjeldendeBehandling(Saksnummer saksnummer) {
        Query query = entityManager.createNativeQuery("SELECT b.behandling_id from GJELDENDE_VEDTATT_BEHANDLING b where b.saksnummer=?1");
        query.setParameter(1, saksnummer.asString());
        query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);
        var result = query.getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(((BigDecimal) query.getSingleResult()).longValue());
    }

    @Override
    public Optional<SakStatus> finnNyesteSakForAnnenPart(String aktørIdBruker, String annenPartAktørId) {
        TypedQuery<SakStatus> query = entityManager.createQuery("from SakStatus where aktørId=:annenPartAktørId and aktørIdAnnenPart=:aktørId and fagsakYtelseType=:ytelseType order by opprettetTidspunkt desc", SakStatus.class);
        query.setParameter("aktørId", aktørIdBruker);
        query.setParameter("ytelseType", FagsakYtelseType.FP.getVerdi());
        query.setParameter("annenPartAktørId", annenPartAktørId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<FagsakRelasjon> hentFagsakRelasjon(String saksnummer) {
        TypedQuery<FagsakRelasjon> query = entityManager.createQuery("from FagsakRelasjon where saksnummer=:saksnummer", FagsakRelasjon.class);
        query.setParameter("saksnummer", new TypedParameterValue(StringType.INSTANCE, saksnummer));
        return query.getResultList().stream()
                .max(Comparator.comparing(FagsakRelasjon::getEndretTidspunkt));
    }

    @Override
    public Behandling hentBehandling(Long behandlingId) {
        return getBehandling(behandlingId);
    }

    @Override
    public List<Behandling> hentTilknyttedeBehandlinger(String saksnummer) {
        TypedQuery<Behandling> query = entityManager.createQuery("from Behandling where saksnummer=:saksnummer", Behandling.class);
        query.setParameter("saksnummer", new TypedParameterValue(StringType.INSTANCE, saksnummer));
        return query.getResultList();
    }

    @Override
    public List<MottattDokument> hentMottatteDokumenter(UUID forsendelseId) {
        TypedQuery<MottattDokument> query = entityManager.createQuery("from MottattDokument where forsendelse_id=:forsendelseId", MottattDokument.class);
        query.setParameter("forsendelseId", forsendelseId);
        return query.getResultList();
    }

    @Override
    public List<MottattDokument> hentInntektsmeldinger(Long behandlingId) {
        TypedQuery<MottattDokument> query = entityManager.createQuery("from MottattDokument where behandling_id=:behandlingId", MottattDokument.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList()
                .stream()
                .filter(o -> DokumentTypeId.INNTEKTSMELDING.getVerdi().equals(o.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MottattDokument> hentMottattDokument(Long behandlingId) {
        TypedQuery<MottattDokument> query = entityManager.createQuery("from MottattDokument where behandling_id=:behandlingId", MottattDokument.class);
        query.setParameter("behandlingId", behandlingId);
        return query.getResultList();
    }

    private List<SakStatus> getSakStatus(String aktørId) {
        TypedQuery<SakStatus> query = entityManager.createQuery("from SakStatus where hoved_soeker_aktoer_id=:aktørId", SakStatus.class);
        query.setParameter("aktørId", aktørId);
        return query.getResultList();
    }

    private Behandling getBehandling(Long behandlingId) {
        TypedQuery<Behandling> query = entityManager.createQuery("from Behandling where behandling_id=:behandlingId", Behandling.class); //$NON-NLS-1$
        query.setParameter("behandlingId", behandlingId); //$NON-NLS-1$
        Behandling behandling = query.getResultList().stream().reduce((first, second) -> second).orElse(null); // TODO(HUMLE): finn riktig implementasjon
        if (behandling == null) {
            throw DokumentForsendelseRepositoryFeil.FACTORY.fantIkkeBehandlingForBehandlingId(behandlingId).toException();
        }
        return behandling;
    }
}
