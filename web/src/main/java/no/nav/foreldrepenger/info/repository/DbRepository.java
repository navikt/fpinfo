package no.nav.foreldrepenger.info.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StringType;

import no.nav.foreldrepenger.info.Aksjonspunkt;
import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.FamilieHendelse;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.SøknadsGrunnlag;
import no.nav.foreldrepenger.info.SøknadsperiodeEntitet;
import no.nav.foreldrepenger.info.UttakPeriode;

@Dependent
public class DbRepository implements Repository {

    private static final String BEHANDLING_ID = "behandlingId";
    private static final String SAKSNUMMER = "saksnummer";

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
                .setParameter(BEHANDLING_ID, behandlingId)
                .getResultList();
    }

    @Override
    public Optional<SøknadsGrunnlag> hentSøknadsGrunnlag(Long behandlingId) {
        return em.createQuery("from SøknadsGrunnlag where behandlingId=:behandlingId", SøknadsGrunnlag.class)
                .setParameter(BEHANDLING_ID, behandlingId)
                .getResultStream()
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
    public List<Behandling> hentTilknyttedeBehandlinger(String saksnummer) {
        return em.createQuery("from Behandling where saksnummer=:saksnummer", Behandling.class)
            .setParameter(SAKSNUMMER, new TypedParameterValue(StringType.INSTANCE, saksnummer))
            .getResultList();
    }

    @Override
    public List<MottattDokument> hentMottattDokument(Long behandlingId) {
        return em.createQuery("from MottattDokument where behandling_id=:behandlingId", MottattDokument.class)
                .setParameter(BEHANDLING_ID, behandlingId).getResultList();
    }

    @Override
    public Set<String> hentBarn(Saksnummer saksnummer) {
        var query = em.createQuery("select distinct aktørIdBarn from SakStatus "
                        + "where saksnummer=:saksnummer "
                        + "and aktørIdBarn is not null", String.class)
                .setParameter(SAKSNUMMER, saksnummer.saksnummer());
        return query.getResultStream().collect(Collectors.toSet());
    }

    @Override
    public List<SøknadsperiodeEntitet> hentSøknadsperioder(long behandlingId) {
        return em.createQuery("from SoeknadPeriode where behandlingId=:b", SøknadsperiodeEntitet.class)
                .setParameter("b", behandlingId).getResultList();
    }

    @Override
    public Optional<FamilieHendelse> hentFamilieHendelse(long behandlingId) {
        return em.createQuery("from FamilieHendelse where behandlingId=:behandlingId", FamilieHendelse.class)
                .setParameter(BEHANDLING_ID, behandlingId).getResultStream().findFirst();
    }

    @Override
    public Set<Aksjonspunkt> hentAksjonspunkt(long behandlingId) {
        return em.createQuery("from Aksjonspunkt where behandlingId=:behandlingId", Aksjonspunkt.class)
                .setParameter(BEHANDLING_ID, behandlingId).getResultStream().collect(Collectors.toSet());
    }
}
