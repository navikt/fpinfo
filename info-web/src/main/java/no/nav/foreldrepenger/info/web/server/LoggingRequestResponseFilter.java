package no.nav.foreldrepenger.info.web.server;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;

@RequestScoped
@BeskyttetRessurs(action = BeskyttetRessursActionAttributt.DUMMY, resource = "")
public class LoggingRequestResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingRequestResponseFilter.class);
    int i;

    @Context
    private UriInfo info;

    public LoggingRequestResponseFilter() {
        LOG.info("FILTER CONSTRUCT {}", info.getMatchedResources());
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        LOG.info("FILTER REQUEST {} {}", this.hashCode(), i);
        i++;

    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        LOG.info("FILTER RESPONSE {} {}", this.hashCode(), i);
        i++;
    }
}
