package no.nav.foreldrepenger.info.web.server.sikkerhet;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;
import no.nav.vedtak.sikkerhet.abac.TokenProvider;

@Dependent
@Priority(1)
public class TokenSupportTokenProvider implements TokenProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TokenSupportTokenProvider.class);

    @Override
    public String getUid() {
        LOG.info("Henter ut uid");
        var token = JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken();
        return token.map(JwtToken::getSubject).orElseThrow();

    }

    @Override
    public String userToken() {
        LOG.info("Henter ut token");
        var token = JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken();
        return token.map(JwtToken::getTokenAsString).orElseThrow();
    }

}
