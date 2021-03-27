package no.nav.foreldrepenger.info.web.server;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration;
import no.nav.security.token.support.jaxrs.servlet.JaxrsJwtTokenValidationFilter;

public class LoggingTokenValidationFilter extends JaxrsJwtTokenValidationFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingTokenValidationFilter.class);

    public LoggingTokenValidationFilter(MultiIssuerConfiguration multiIssuerConfiguration) {
        super(multiIssuerConfiguration);
        LOG.info("CONSTRUCT");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.info("DOFILTER " + HttpServletRequest.class.cast(request).getRequestURI());
        super.doFilter(request, response, chain);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.info("INIT");
        super.init(filterConfig);
    }

}
