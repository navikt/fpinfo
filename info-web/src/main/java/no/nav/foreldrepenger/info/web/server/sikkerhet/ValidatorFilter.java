package no.nav.foreldrepenger.info.web.server.sikkerhet;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;

@Provider
public class ValidatorFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ValidatorFilter.class);

    public ValidatorFilter() {
        LOG.info("Konstruert");
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            var token = JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken();
            if (token.isPresent()) {
                LOG.info("Vi har et token");
            } else {
                LOG.info("Vi har ikke token");
            }
        } catch (Exception e) {
            LOG.info("OOPS", e);
        }
    }
}
