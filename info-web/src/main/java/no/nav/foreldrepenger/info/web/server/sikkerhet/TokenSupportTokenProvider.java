package no.nav.foreldrepenger.info.web.server.sikkerhet;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;
import no.nav.vedtak.sikkerhet.abac.TokenProvider;

@Alternative
@Dependent
@Priority(100)
public class TokenSupportTokenProvider implements TokenProvider {

    @Override
    public String getUid() {
        return JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken()
                .map(JwtToken::getSubject)
                .orElseThrow();
    }

    @Override
    public String userToken() {
        return JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken()
                .map(JwtToken::getTokenAsString)
                .orElseThrow();
    }
}
