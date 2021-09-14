package no.nav.foreldrepenger.info.repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.FagsakRelasjon;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.UttakPeriode;
import no.nav.foreldrepenger.info.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.datatyper.FagsakYtelseType;
import no.nav.vedtak.exception.TekniskException;

@Dependent
public class DbRepository implements Repository {

    private static final Logger LOG = LoggerFactory.getLogger(DbRepository.class);

    private final EntityManager em;

    @Inject
    public DbRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Sak> hentSak(String aktørId) {
        return em.createQuery("from SakStatus where hoved_soeker_aktoer_id=:aktørId", Sak.class)
                .setParameter("aktørId", aktørId).getResultList();
    }

    @Override
    public List<UttakPeriode> hentUttakPerioder(Long behandlingId) {
        return em.createQuery("from UttakPeriode where behandlingId=:behandlingId", UttakPeriode.class)
                .setParameter("behandlingId", behandlingId)
                .getResultList();
    }

    @Override
    public Optional<SøknadsGrunnlag> hentSøknadsGrunnlag(Long behandlingId) {
        return em.createQuery("from SøknadsGrunnlag where behandlingId=:behandlingId", SøknadsGrunnlag.class)
                .setParameter("behandlingId",
                        behandlingId)
                .getResultList().stream()
                .reduce((first, second) -> second);
    }

    @Override
    public Optional<Long> hentGjeldendeBehandling(Saksnummer saksnummer) {
        var query = em.createNativeQuery("SELECT b.behandling_id from GJELDENDE_VEDTATT_BEHANDLING b where b.saksnummer=?1")
                .setParameter(1, saksnummer.saksnummer())
                .setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);
        var result = query.getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(((BigDecimal) query.getSingleResult()).longValue());
    }

    @Override
    public Optional<Sak> finnNyesteSakForAnnenPart(String aktørIdBruker, String annenPartAktørId) {
        return em.createQuery(
                "from SakStatus where aktørId=:annenPartAktørId and aktørIdAnnenPart=:aktørId and fagsakYtelseType=:ytelseType order by opprettetTidspunkt desc",
                Sak.class)
                .setParameter("aktørId", aktørIdBruker)
                .setParameter("ytelseType", FagsakYtelseType.FP.name())
                .setParameter("annenPartAktørId", annenPartAktørId).getResultList().stream()
                .findFirst();
    }

    @Override
    public Optional<FagsakRelasjon> hentFagsakRelasjon(String saksnummer) {
        return em.createQuery("from FagsakRelasjon where saksnummer=:saksnummer", FagsakRelasjon.class)
                .setParameter("saksnummer",
                        new TypedParameterValue(StringType.INSTANCE, saksnummer))
                .getResultList()
                .stream()
                .max(Comparator.comparing(FagsakRelasjon::getEndretTidspunkt));
    }

    @Override
    public Behandling hentBehandling(Long behandlingId) {
        var resultList = em.createQuery("from Behandling where behandling_id=:behandlingId", Behandling.class)
                .setParameter("behandlingId", behandlingId).getResultList();
        if (resultList.size() > 1) {
            LOG.info("Hent behandling med id {} returnerte {} behandlinger", behandlingId, resultList.size());
        }
        return resultList.stream().findFirst()
                .orElseThrow(() -> new TekniskException("FP-741456", String.format("Fant ingen behandling med behandlingId: %s", behandlingId)));
    }

    @Override
    public List<Behandling> hentTilknyttedeBehandlinger(String saksnummer) {
        return em.createQuery("from Behandling where saksnummer=:saksnummer", Behandling.class)
                .setParameter("saksnummer", new TypedParameterValue(StringType.INSTANCE, saksnummer)).getResultList();
    }

    @Override
    public List<MottattDokument> hentMottatteDokumenter(UUID forsendelseId) {
        return em.createQuery("from MottattDokument where forsendelse_id=:forsendelseId", MottattDokument.class)
                .setParameter("forsendelseId", forsendelseId).getResultList();
    }

    @Override
    public List<MottattDokument> hentInntektsmeldinger(Long behandlingId) {
        return em.createQuery("from MottattDokument where behandling_id=:behandlingId", MottattDokument.class)
                .setParameter("behandlingId", behandlingId).getResultList()
                .stream()
                .filter(o -> DokumentTypeId.INNTEKTSMELDING.name().equals(o.getType()))
                .toList();
    }

    @Override
    public List<MottattDokument> hentMottattDokument(Long behandlingId) {
        return em.createQuery("from MottattDokument where behandling_id=:behandlingId", MottattDokument.class)
                .setParameter("behandlingId", behandlingId).getResultList();
    }
}
