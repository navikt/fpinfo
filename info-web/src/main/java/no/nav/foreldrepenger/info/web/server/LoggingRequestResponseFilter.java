package no.nav.foreldrepenger.info.web.server;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class LoggingRequestResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingRequestResponseFilter.class);
    int i;

    // @Context
    // private UriInfo info;

    public LoggingRequestResponseFilter() {
        LOG.info("FILTER CONSTRUCT {}", this);
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
