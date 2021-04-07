package no.nav.foreldrepenger.info.web.server;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class LoggingRequestResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingRequestResponseFilter.class);
    int i;

    public LoggingRequestResponseFilter() {
        LOG.info("CONSTRUCT");
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        LOG.info("FILTER REQUEST " + i);
        i++;

    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        LOG.info("FILTER RESPONSE " + i);
        i++;
    }
}
