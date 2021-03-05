package no.nav.foreldrepenger.info.web.server.sikkerhet;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;

import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;
import no.nav.vedtak.sikkerhet.abac.TokenProvider;

@Dependent
@Priority(1)
public class TokenSupportTokenProvider implements TokenProvider {

    @Override
    public String getUid() {
        var token = JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken();
        return token.map(JwtToken::getSubject).orElseThrow();

    }

    @Override
    public String userToken() {
        var token = JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken();
        return token.map(JwtToken::getTokenAsString).orElseThrow();
    }

}
