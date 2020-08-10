package no.nav.foreldrepenger.info.web.server.abac;

import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_DOMENE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_ACTION_ACTION_ID;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.abac.AppAbacAttributtType;
import no.nav.foreldrepenger.info.pip.PipRepository;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PdpRequestBuilderImpl.class);

    public static final String ABAC_DOMAIN = "foreldrepenger";
    private PipRepository pipRepository;

    @Inject
    public PdpRequestBuilderImpl(PipRepository pipRepository) {
        this.pipRepository = pipRepository;
    }

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        PdpRequest pdpRequest = new PdpRequest();
        pdpRequest.put(RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(XACML10_ACTION_ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource());

        if (attributter.getVerdier(AppAbacAttributtType.ANNEN_PART).size() == 1) {
            LOGGER.info("Annen_part finnes");
            var aktørId = (String)attributter.getVerdier(AppAbacAttributtType.AKTØR_ID).stream().findFirst().orElseThrow();
            var annenpartAktørId = (String)attributter.getVerdier(AppAbacAttributtType.ANNEN_PART).stream().findFirst().orElseThrow();
            pdpRequest.put(AppAbacAttributtType.RESOURCE_FORELDREPENGER_ANNEN_PART, annenpartAktørId);

            var saksnummerAnnenpart = pipRepository.finnSaksnummerTilAnnenpart(aktørId, annenpartAktørId);
            if (saksnummerAnnenpart.isPresent()) {
                var erAleneomsorg = pipRepository.erAleneomsorg(saksnummerAnnenpart.get()).orElse(null);
                pdpRequest.put(AppAbacAttributtType.RESOURCE_FORELDREPENGER_ALENEOMSORG, erAleneomsorg);
                LOGGER.info("Annen_part finnes - sak {}", erAleneomsorg);
            }
        }
        pdpRequest.put(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, utledAktørIds(attributter));
        return pdpRequest;
    }

    private Set<String> utledAktørIds(AbacAttributtSamling attributter) {
        Set<String> aktørIds = new HashSet<>(attributter.getVerdier(AppAbacAttributtType.AKTØR_ID));
        aktørIds.addAll(
                pipRepository.hentAktørIdForSaksnummer(attributter.getVerdier(AppAbacAttributtType.SAKSNUMMER)));
        aktørIds.addAll(pipRepository
                .hentAktørIdForBehandling(attributter.getVerdier(AppAbacAttributtType.BEHANDLING_ID)));
        aktørIds.addAll(pipRepository
                .hentAktørIdForForsendelseIder(attributter.getVerdier(AppAbacAttributtType.FORSENDELSE_UUID)));
        return aktørIds;
    }

}
