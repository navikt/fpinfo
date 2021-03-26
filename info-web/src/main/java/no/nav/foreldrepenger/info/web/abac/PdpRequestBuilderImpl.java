package no.nav.foreldrepenger.info.web.abac;

import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_DOMENE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.SUBJECT_LEVEL;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.SUBJECT_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_ACTION_ACTION_ID;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_SUBJECT_ID;
import static no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType.AKTØR_ID;
import static no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType.BEHANDLING_ID;
import static no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType.SAKSNUMMER;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jwt.SignedJWT;

import no.nav.foreldrepenger.info.pip.PipRepository;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.AbacIdToken.TokenType;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;

/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@Dependent
@Alternative
@Priority(2)
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PdpRequestBuilderImpl.class);

    public static final String ABAC_DOMAIN = "foreldrepenger";
    private PipRepository pipRepository;

    @Inject
    public PdpRequestBuilderImpl(PipRepository pipRepository) {
        this.pipRepository = pipRepository;
    }

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        LOG.trace("Lager PDP request");
        PdpRequest pdpRequest = new PdpRequest();
        pdpRequest.put(RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(XACML10_ACTION_ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource());
        if (attributter.getIdToken().getTokenType().equals(TokenType.TOKENX)) {
            LOG.trace("Legger til ekstra tokenX attributter");
            pdpRequest.put(XACML10_SUBJECT_ID, claim(attributter.getIdToken().getToken(), "sub"));
            pdpRequest.put(SUBJECT_LEVEL, claim(attributter.getIdToken().getToken(), "acr"));
            pdpRequest.put(SUBJECT_TYPE, "EksternBruker");
        }

        if (attributter.getVerdier(AppAbacAttributtType.ANNEN_PART).size() == 1) {
            LOG.info("abac Attributter inneholder annen part");
            Optional<String> sakAnnenPart = pipRepository.finnSakenTilAnnenForelder(
                    attributter.getVerdier(AKTØR_ID),
                    attributter.getVerdier(AppAbacAttributtType.ANNEN_PART));
            if (sakAnnenPart.isPresent()) {
                String saksnummerAnnenForelder = sakAnnenPart.get();
                pdpRequest.put(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, new HashSet<>(
                        pipRepository.hentAktørIdForSaksnummer(Collections.singleton(saksnummerAnnenForelder))));
                pdpRequest.put(AppAbacAttributtType.RESOURCE_FORELDREPENGER_ANNEN_PART,
                        pipRepository.hentAnnenPartForSaksnummer(saksnummerAnnenForelder).orElse(null));
                pdpRequest.put(AppAbacAttributtType.RESOURCE_FORELDREPENGER_ALENEOMSORG,
                        pipRepository.hentOppgittAleneomsorgForSaksnummer(saksnummerAnnenForelder).orElse(null));
            }
        } else {
            pdpRequest.put(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, utledAktørIdeer(attributter));
        }
        LOG.trace("Laget PDP request OK {}", pdpRequest);
        return pdpRequest;
    }

    private String claim(String token, String claim) {
        try {
            var claims = SignedJWT.parse(token).getJWTClaimsSet();
            return String.class.cast(claims.getClaim(claim));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Fant ikke claim" + claim + " i token", e);
        }
    }

    private Set<String> utledAktørIdeer(AbacAttributtSamling attributter) {
        Set<String> aktørIdSet = new HashSet<>(attributter.getVerdier(AKTØR_ID));
        aktørIdSet.addAll(
                pipRepository.hentAktørIdForSaksnummer(attributter.getVerdier(SAKSNUMMER)));
        aktørIdSet.addAll(pipRepository
                .hentAktørIdForBehandling(attributter.getVerdier(BEHANDLING_ID)));
        aktørIdSet.addAll(pipRepository
                .hentAktørIdForForsendelseIder(attributter.getVerdier(AppAbacAttributtType.FORSENDELSE_UUID)));
        return aktørIdSet;
    }

}
