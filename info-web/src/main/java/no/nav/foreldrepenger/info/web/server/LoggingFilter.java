package no.nav.foreldrepenger.info.web.server;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(dispatcherTypes = DispatcherType.REQUEST, urlPatterns = "/api/*")
public class LoggingFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    public LoggingFilter() {
        LOG.info("CONSTRUCT");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.info("DOFILTER");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.info("INIT");
    }

}
