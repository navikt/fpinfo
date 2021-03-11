package no.nav.foreldrepenger.info.web.server.sikkerhet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration;
import no.nav.security.token.support.jaxrs.servlet.JaxrsJwtTokenValidationFilter;

public class LoggingJaxrsJwtTokenValidationFilter extends JaxrsJwtTokenValidationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingJaxrsJwtTokenValidationFilter.class);

    public LoggingJaxrsJwtTokenValidationFilter(MultiIssuerConfiguration oidcConfig) {
        super(oidcConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        super.init(filterConfig);
    }

}
