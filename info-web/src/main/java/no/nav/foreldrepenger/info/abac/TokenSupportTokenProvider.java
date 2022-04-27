package no.nav.foreldrepenger.info.abac;

import java.net.URI;
import java.text.ParseException;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import no.nav.security.token.support.core.jwt.JwtTokenClaims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jwt.SignedJWT;

import no.nav.foreldrepenger.sikkerhet.abac.domene.TokenType;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.jaxrs.JaxrsTokenValidationContextHolder;

@ApplicationScoped
public class TokenSupportTokenProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TokenSupportTokenProvider.class);

    public String getUid() {
        return firstToken("UID")
                .map(JwtToken::getJwtTokenClaims)
                .map(TokenSupportTokenProvider::uid)
                .orElseThrow();
    }

    public String userToken() {
        return firstToken("USER")
                .map(JwtToken::getTokenAsString)
                .orElseThrow();
    }

    public TokenType getTokeType() {
        return firstToken("ISSUER")
                .map(JwtToken::getIssuer)
                .map(URI::create)
                .map(URI::getHost)
                .map(host -> host.contains("tokendings") ? TokenType.TOKENX : TokenType.OIDC)
                .orElseThrow(() -> new IllegalArgumentException("Ukjent token type"));
    }

    private Optional<JwtToken> firstToken(String type) {
        var token = JaxrsTokenValidationContextHolder.getHolder().getTokenValidationContext().getFirstValidToken();
        token.ifPresent(t -> LOG.trace("{} Issuer {}", type, t.getIssuer()));
        return token;
    }

    public static String claim(String token, String claim) {
        try {
            return String.class.cast(SignedJWT.parse(token).getJWTClaimsSet().getClaim(claim));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Fant ikke claim " + claim + " i token", e);
        }
    }

    private static String uid(JwtTokenClaims claims) {
        return Optional.ofNullable(claims.getStringClaim("pid"))
                .orElseGet(() -> claims.getStringClaim("sub"));
    }

}
