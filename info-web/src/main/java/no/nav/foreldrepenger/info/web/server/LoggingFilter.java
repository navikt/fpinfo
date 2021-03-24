package no.nav.foreldrepenger.info.web.server;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.http.HttpRequest;
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler;
import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;

public class LoggingFilter implements Filter {
    private final JwtTokenValidationHandler jwtTokenValidationHandler;
    private final TokenValidationContextHolder contextHolder;
    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    public LoggingFilter(MultiIssuerConfiguration oidcConfig) {
        LOG.info("CONSTRUCT");
        this.jwtTokenValidationHandler = new JwtTokenValidationHandler(oidcConfig);
        this.contextHolder = JaxrsTokenValidationContextHolder.getHolder();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.info("Filter exec med issuers {}", contextHolder.getTokenValidationContext().getIssuers());
        if (request instanceof HttpServletRequest) {
            doTokenValidation((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.info("INIT");

    }

    private void doTokenValidation(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var validatedTokens = jwtTokenValidationHandler.getValidatedTokens(fromHttpServletRequest(request));
        LOG.info("Validated tokens {}", validatedTokens);
        contextHolder.setTokenValidationContext(validatedTokens);
        try {
            chain.doFilter(request, response);
        } finally {
            contextHolder.setTokenValidationContext(null);
        }
    }

    static HttpRequest fromHttpServletRequest(final HttpServletRequest request) {
        return new HttpRequest() {
            @Override
            public String getHeader(String headerName) {
                return request.getHeader(headerName);
            }

            @Override
            public NameValue[] getCookies() {
                if (request.getCookies() == null) {
                    return null;
                }
                return Arrays.stream(request.getCookies()).map(cookie -> new NameValue() {

                    @Override
                    public String getName() {
                        return cookie.getName();
                    }

                    @Override
                    public String getValue() {
                        return cookie.getValue();
                    }
                }).toArray(NameValue[]::new);
            }
        };
    }
}
