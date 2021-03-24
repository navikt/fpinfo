package no.nav.foreldrepenger.info.web.server.sikkerhet;

import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;
import no.nav.vedtak.sikkerhet.abac.TokenProvider;

@Alternative
@Dependent
@Priority(100)
public class TokenSupportTokenProvider implements TokenProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TokenSupportTokenProvider.class);

    @Override
    public String getUid() {
        return firstToken()
                .map(JwtToken::getSubject)
                .orElseThrow();
    }

    @Override
    public String userToken() {
        return firstToken()
                .map(JwtToken::getTokenAsString)
                .orElseThrow();
    }

    private Optional<JwtToken> firstToken() {
        var token = JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken();
        token.ifPresent(t -> LOG.trace("Issuer {}", t.getIssuer()));
        return token;
    }
}
