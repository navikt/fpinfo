package no.nav.foreldrepenger.info.abac;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;
import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;
import no.nav.vedtak.sikkerhet.abac.TokenProvider;
import no.nav.vedtak.sikkerhet.oidc.config.ConfigProvider;
import no.nav.vedtak.sikkerhet.oidc.config.OpenIDConfiguration;
import no.nav.vedtak.sikkerhet.oidc.token.OpenIDToken;
import no.nav.vedtak.sikkerhet.oidc.token.TokenString;

@ApplicationScoped
public class TokenSupportTokenProvider implements TokenProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TokenSupportTokenProvider.class);

    @Override
    public String getUid() {
        return firstToken("UID")
                .map(JwtToken::getJwtTokenClaims)
                .map(TokenSupportTokenProvider::uid)
                .orElseThrow();
    }

    @Override
    public OpenIDToken openIdToken() {
        var provider = firstToken("ISSUER")
                .map(JwtToken::getIssuer)
                .flatMap(ConfigProvider::getOpenIDConfiguration)
                .map(OpenIDConfiguration::type)
                .orElseThrow(() -> new IllegalArgumentException("Ukjent tokenprovider"));
        return firstToken("USER")
                .map(JwtToken::getTokenAsString)
                .map(t -> new OpenIDToken(provider, new TokenString(t)))
                .orElseThrow();
    }

    @Override
    public String samlToken() {
        return null;
    }

    private Optional<JwtToken> firstToken(String type) {
        var token = JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken();
        token.ifPresent(t -> LOG.trace("{} Issuer {}", type, t.getIssuer()));
        return token;
    }

    private static String uid(JwtTokenClaims claims) {
        return Optional.ofNullable(claims.getStringClaim("pid"))
                .orElseGet(() -> claims.getStringClaim("sub"));
    }
}
