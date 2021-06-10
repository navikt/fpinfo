package no.nav.foreldrepenger.info.web.abac;

import static no.nav.foreldrepenger.info.web.abac.TokenSupportTokenProvider.claim;
import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.AKTØR_ID;
import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.BEHANDLING_ID;
import static no.nav.foreldrepenger.sikkerhet.abac.domene.StandardAbacAttributtType.SAKSNUMMER;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.pip.PipRepository;
import no.nav.foreldrepenger.sikkerhet.abac.PdpRequestBuilder;
import no.nav.foreldrepenger.sikkerhet.abac.domene.BeskyttRessursAttributer;
import no.nav.foreldrepenger.sikkerhet.abac.domene.IdSubject;
import no.nav.foreldrepenger.sikkerhet.abac.domene.IdToken;
import no.nav.foreldrepenger.sikkerhet.abac.domene.TokenType;
import no.nav.foreldrepenger.sikkerhet.abac.pep.PdpRequest;

/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@Dependent
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PdpRequestBuilderImpl.class);
    private static final String ABAC_DOMAIN = "foreldrepenger";
    protected static final String PEP_ID = "fpinfo";

    private final PipRepository pipRepository;
    private final TokenSupportTokenProvider tokenProvider;

    @Inject
    public PdpRequestBuilderImpl(PipRepository pipRepository, TokenSupportTokenProvider tokenProvider) {
        this.pipRepository = pipRepository;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public PdpRequest lagPdpRequest(BeskyttRessursAttributer requestAttributer) {
        LOG.trace("Lager PDP request");
        var tokeType = tokenProvider.getTokeType();
        var subjectId = tokenProvider.getUid();
        var token = tokenProvider.userToken();

        var pdpRequest = PdpRequest.builder()
                .medRequest(requestAttributer.getRequestPath())
                .medActionType(requestAttributer.getActionType())
                .medResourceType(requestAttributer.getResource())
                .medUserId(subjectId)
                .medIdToken(IdToken.withToken(token, tokeType))
                .medDomene(ABAC_DOMAIN)
                .medPepId(PEP_ID)
                .build();

        if (tokeType.equals(TokenType.TOKENX)) {
            LOG.trace("Legger til ekstra tokenX attributter");
            pdpRequest.setIdSubject(IdSubject.with(subjectId, "EksternBruker", Integer.parseInt(claim(token, "acr").replace("Level", ""))));
        }

        if (requestAttributer.getVerdier(AppAbacAttributtType.ANNEN_PART).size() == 1) {
            LOG.info("abac Attributter inneholder annen part");
            var sakAnnenPart = pipRepository.finnSakenTilAnnenForelder(
                    requestAttributer.getVerdier(AKTØR_ID),
                    requestAttributer.getVerdier(AppAbacAttributtType.ANNEN_PART));

            sakAnnenPart.ifPresent(saksnummerAnnenForelder -> {
                pdpRequest.setAktørIder(new HashSet<>(
                        pipRepository.hentAktørIdForSaksnummer(Collections.singleton(saksnummerAnnenForelder))));
                pdpRequest.setAnnenPartAktørId(pipRepository.hentAnnenPartForSaksnummer(saksnummerAnnenForelder).orElse(null));
                pdpRequest.setAleneomsorg(pipRepository.hentOppgittAleneomsorgForSaksnummer(saksnummerAnnenForelder).orElse(null));
            });
        } else {
            pdpRequest.setAktørIder(utledAktørIdeer(requestAttributer));
        }
        LOG.trace("Laget PDP request OK {}", pdpRequest);
        return pdpRequest;
    }

    private Set<String> utledAktørIdeer(BeskyttRessursAttributer attributter) {
        Set<String> aktørIdSet = new HashSet<>(attributter.getVerdier(AKTØR_ID));
        aktørIdSet.addAll(
                pipRepository.hentAktørIdForSaksnummer(attributter.getVerdier(SAKSNUMMER)));
        aktørIdSet.addAll(pipRepository
                .hentAktørIdForBehandling(attributter.getVerdier(BEHANDLING_ID)));
        aktørIdSet.addAll(pipRepository
                .hentAktørIdForForsendelseIder(attributter.getVerdier(AppAbacAttributtType.FORSENDELSE_UUID)));
        return aktørIdSet;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pipRepository=" + pipRepository + ", tokenProvider=" + tokenProvider + "]";
    }
}
