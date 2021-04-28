package no.nav.foreldrepenger.info.web.server;

import static no.nav.foreldrepenger.log.mdc.MDCOperations.NAV_CALL_ID;
import static no.nav.foreldrepenger.log.mdc.MDCOperations.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.log.mdc.MDCOperations.generateCallId;
import static no.nav.foreldrepenger.log.mdc.MDCOperations.putToMDC;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter
public class HeadersToMDCFilterBean implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterBean.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        toMDC(HttpServletRequest.class.cast(req));
        chain.doFilter(req, res);
    }

    private void toMDC(HttpServletRequest req) {
        try {
            putToMDC(NAV_CONSUMER_ID, req.getHeader(NAV_CONSUMER_ID));
            putToMDC(NAV_CALL_ID, req.getHeader(NAV_CALL_ID), generateCallId());
        } catch (Exception e) {
            LOG.warn("Noe gikk galt ved setting av MDC-verdier for request {}, MDC-verdier er inkomplette",
                    req.getRequestURI(), e);
        }
    }
}
