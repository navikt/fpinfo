package no.nav.foreldrepenger.info.web.server.abac;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import no.nav.abac.common.xacml.CommonAttributter;
import no.nav.abac.foreldrepenger.xacml.ForeldrepengerAttributter;
import no.nav.foreldrepenger.info.abac.UtvidetAbacAttributtType;
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
    public static final String ABAC_DOMAIN = "foreldrepenger";
    private PipRepository pipRepository;

    @Inject
    public PdpRequestBuilderImpl(PipRepository pipRepository) {
        this.pipRepository = pipRepository;
    }

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        PdpRequest pdpRequest = new PdpRequest();
        pdpRequest.put(CommonAttributter.RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(CommonAttributter.XACML_1_0_ACTION_ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(CommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource().getEksternKode());

        if (attributter.getVerdier(UtvidetAbacAttributtType.ANNEN_PART).size() == 1) {
            Optional<String> sakAnnenPart = pipRepository.finnSakenTilAnnenForelder(attributter.getAktørIder(), attributter.getVerdier(UtvidetAbacAttributtType.ANNEN_PART));
            if(sakAnnenPart.isPresent()) {
                String saksnummerAnnenForelder = sakAnnenPart.get();
                pdpRequest.put(CommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, new HashSet<>(pipRepository.hentAktørIdForSaksnummer(Collections.singleton(saksnummerAnnenForelder))));
                pdpRequest.put(ForeldrepengerAttributter.RESOURCE_FORELDREPENGER_ANNEN_PART, pipRepository.hentAnnenPartForSaksnummer(saksnummerAnnenForelder).orElse(null));
                pdpRequest.put(ForeldrepengerAttributter.RESOURCE_FORELDREPENGER_ALENEOMSORG, pipRepository.hentOppgittAleneomsorgForSaksnummer(saksnummerAnnenForelder).orElse(null));
            }
        } else {
            pdpRequest.put(CommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, utledAktørIdeer(attributter));
        }
        return pdpRequest;
    }

    private Set<String> utledAktørIdeer(AbacAttributtSamling attributter) {
        Set<String> aktørIdSet = new HashSet<>();
        aktørIdSet.addAll(attributter.getAktørIder());
        aktørIdSet.addAll(pipRepository.hentAktørIdForSaksnummer(attributter.getSaksnummre()));
        aktørIdSet.addAll(pipRepository.hentAktørIdForBehandling(attributter.getBehandlingsIder()));
        aktørIdSet.addAll(pipRepository.hentAktørIdForForsendelseIder(attributter.getDokumentforsendelseIder()));

        return aktørIdSet;
    }
}
