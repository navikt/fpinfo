package no.nav.foreldrepenger.info.web.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.weld.interceptor.util.proxy.TargetInstanceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.log.sporingslogg.Sporingsdata;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.AbacAuditlogger;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.AbacSporingslogg;
import no.nav.vedtak.sikkerhet.abac.ActionUthenter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.Pep;
import no.nav.vedtak.sikkerhet.abac.PepNektetTilgangException;
import no.nav.vedtak.sikkerhet.abac.Tilgangsbeslutning;
import no.nav.vedtak.sikkerhet.abac.TilpassetAbacAttributt;
import no.nav.vedtak.sikkerhet.abac.TokenProvider;
import no.nav.vedtak.util.env.Environment;

@RequestScoped
@Provider
public class LoggingRequestResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingRequestResponseFilter.class);
    int i;

    private Pep pep;
    private AbacSporingslogg sporingslogg;
    private AbacAuditlogger abacAuditlogger;
    private static final Environment ENV = Environment.current();
    private TokenProvider tokenProvider;
    private List<Sporingsdata> sporingsdata;

    public LoggingRequestResponseFilter() {

    }

    @Inject
    public LoggingRequestResponseFilter(Pep pep,
            AbacSporingslogg sporingslogg, AbacAuditlogger abacAuditlogger,
            TokenProvider tokenProvider) {
        this.pep = pep;
        this.sporingslogg = sporingslogg;
        this.abacAuditlogger = abacAuditlogger;
        this.tokenProvider = tokenProvider;
        LOG.info("FILTER CONSTRUCT {}", this);
    }

    @Override
    public void filter(ContainerRequestContext ctx) {
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) ctx.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        Method method = methodInvoker.getMethod();
        methodInvoker.getResourceClass();
        LOG.info("FILTER REQUEST {} {} method {}", this, i, method);
        i++;
        var attributter = hentAttributter(method);
        LOG.info("ATTRIBUTTER " + attributter);
        /*
         * var beslutning = pep.vurderTilgang(attributter); if
         * (beslutning.fikkTilgang()) { sporingsdata = proceed(method, attributter,
         * beslutning); } ikkeTilgang(attributter, beslutning);
         */

    }

    private List<Sporingsdata> proceed(Method method, AbacAttributtSamling attributter, Tilgangsbeslutning beslutning) throws Exception {
        boolean sporingslogges = method.getAnnotation(BeskyttetRessurs.class).sporingslogg();
        if (sporingslogges) {
            if (abacAuditlogger.isEnabled()) {
                abacAuditlogger.loggTilgang(tokenProvider.getUid(), beslutning.getPdpRequest(), attributter);
            }
            return sporingslogg.byggSporingsdata(beslutning, attributter);
        }
        return null;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        LOG.info("FILTER RESPONSE {} {}", this, i);
        i++;
        if (!abacAuditlogger.isEnabled()) {
            sporingslogg.logg(sporingsdata);
        }
    }

    private void ikkeTilgang(AbacAttributtSamling attributter, Tilgangsbeslutning beslutning) {
        if (abacAuditlogger.isEnabled()) {
            final String uid = tokenProvider.getUid();
            abacAuditlogger.loggDeny(uid, beslutning.getPdpRequest(), attributter);
        } else {
            sporingslogg.loggDeny(beslutning, attributter);
        }

        switch (beslutning.getBeslutningKode()) {
            case AVSLÅTT_KODE_6:
                throw new PepNektetTilgangException("F-709170", "Tilgangskontroll.Avslag.Kode6");
            case AVSLÅTT_KODE_7:
                throw new PepNektetTilgangException("F-027901", "Tilgangskontroll.Avslag.Kode7");
            case AVSLÅTT_EGEN_ANSATT:
                throw new PepNektetTilgangException("F-788257", "Tilgangskontroll.Avslag.EgenAnsatt");
            default:
                throw new PepNektetTilgangException("F-608625", "Ikke tilgang");
        }
    }

    private AbacAttributtSamling hentAttributter(Method method) {
        Class<?> clazz = getOpprinneligKlasse(method);
        var attributter = AbacAttributtSamling.medJwtToken(tokenProvider.userToken());
        var beskyttetRessurs = method.getAnnotation(BeskyttetRessurs.class);
        attributter.setActionType(beskyttetRessurs.action());

        if (!beskyttetRessurs.property().isEmpty()) {
            var resource = ENV.getProperty(beskyttetRessurs.property());
            attributter.setResource(resource);
        } else if (!beskyttetRessurs.resource().isEmpty()) {
            attributter.setResource(beskyttetRessurs.resource());
        }

        attributter.setAction(utledAction(clazz, method));
        var parameterDecl = method.getParameters();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Object parameterValue = method.getParameters()[i];
            LOG.trace("Parameter value {} {}", i, parameterValue.getClass().getSimpleName());
            var tilpassetAnnotering = parameterDecl[i].getAnnotation(TilpassetAbacAttributt.class);
            leggTilAttributterFraParameter(attributter, parameterValue, tilpassetAnnotering);
        }
        return attributter;
    }

    @SuppressWarnings("rawtypes")
    static void leggTilAttributterFraParameter(AbacAttributtSamling attributter, Object parameterValue, TilpassetAbacAttributt tilpassetAnnotering) {
        if (tilpassetAnnotering != null) {
            LOG.trace("Parameter ikke tilpasset");
            leggTil(attributter, tilpassetAnnotering, parameterValue);
        } else {
            if (parameterValue instanceof AbacDto) {
                LOG.trace("Parameter er  abacdto");
                attributter.leggTil(((AbacDto) parameterValue).abacAttributter());
            } else if (parameterValue instanceof Collection) {
                leggTilAbacDtoSamling(attributter, (Collection) parameterValue);
            }
        }
    }

    private static void leggTilAbacDtoSamling(AbacAttributtSamling attributter, Collection<?> parameterValue) {
        for (Object value : parameterValue) {
            if (value instanceof AbacDto) {
                attributter.leggTil(((AbacDto) value).abacAttributter());
            } else {
                throw new TekniskException("F-261962",
                        String.format("Ugyldig input forventet at samling inneholdt bare AbacDto-er, men fant %s",
                                value != null ? value.getClass().getName() : "null"));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static Class<?> getOpprinneligKlasse(Method method) {
        Object target = method.getDeclaringClass();
        if (target instanceof TargetInstanceProxy) {
            return ((TargetInstanceProxy) target).weld_getTargetClass();
        }
        return target.getClass();
    }

    private static String utledAction(Class<?> clazz, Method method) {
        return ActionUthenter.action(clazz, method);
    }

    private static void leggTil(AbacAttributtSamling attributter, TilpassetAbacAttributt tilpassetAnnotering, Object verdi) {
        try {
            var dataAttributter = tilpassetAnnotering.supplierClass().getDeclaredConstructor().newInstance().apply(verdi);
            attributter.leggTil(dataAttributter);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pep=" + pep + ", sporingslogg=" + sporingslogg + ", abacAuditlogger=" + abacAuditlogger
                + ", tokenProvider=" + tokenProvider + "]";
    }
}
