package no.nav.vedtak.sikkerhet.abac;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;

public class ValidatorFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ValidatorFilter.class);

    public ValidatorFilter() {
        LOG.info("Konstruert");
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            LOG.info("Request");
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
