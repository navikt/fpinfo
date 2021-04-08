package no.nav.foreldrepenger.info.web.server;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.sikkerhet.abac.AbacAuditlogger;
import no.nav.vedtak.sikkerhet.abac.AbacSporingslogg;
import no.nav.vedtak.sikkerhet.abac.Pep;
import no.nav.vedtak.sikkerhet.abac.TokenProvider;
import no.nav.vedtak.util.env.Environment;

@RequestScoped
@Provider
public class LoggingRequestResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingRequestResponseFilter.class);
    int i;

    private final Pep pep;
    private final AbacSporingslogg sporingslogg;
    private final AbacAuditlogger abacAuditlogger;
    private static final Environment ENV = Environment.current();
    private final TokenProvider tokenProvider;

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
    public void filter(ContainerRequestContext containerRequestContext) {
        LOG.info("FILTER REQUEST {} {}", this, i);
        i++;

    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        LOG.info("FILTER RESPONSE {} {}", this, i);
        i++;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pep=" + pep + ", sporingslogg=" + sporingslogg + ", abacAuditlogger=" + abacAuditlogger
                + ", tokenProvider=" + tokenProvider + "]";
    }
}
