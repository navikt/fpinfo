package no.nav.foreldrepenger.info.abac;


import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.info.repository.PipRepository;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.pdp.AppRessursData;
import no.nav.vedtak.sikkerhet.abac.pdp.ForeldrepengerDataKeys;

/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@Dependent
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PdpRequestBuilderImpl.class);

    private final PipRepository pipRepository;

    @Inject
    public PdpRequestBuilderImpl(PipRepository pipRepository) {
        this.pipRepository = pipRepository;
    }

    @Override
    public AppRessursData lagAppRessursData(AbacDataAttributter dataAttributter) {
        LOG.trace("Lager PDP app ressursdata");

        var builder= AppRessursData.builder()
                .leggTilAktørIdSet(dataAttributter.getVerdier(StandardAbacAttributtType.AKTØR_ID));

        if (dataAttributter.getVerdier(AppAbacAttributtType.ANNEN_PART).size() == 1) {
            LOG.info("abac Attributter inneholder annen part");
            var sakAnnenPart = pipRepository.finnSakenTilAnnenForelder(
                    dataAttributter.getVerdier(StandardAbacAttributtType.AKTØR_ID),
                    dataAttributter.getVerdier(AppAbacAttributtType.ANNEN_PART));

            sakAnnenPart.ifPresent(saksnummerAnnenForelder -> {
                builder.leggTilAktørIdSet(pipRepository.hentAktørIdForSaksnummer(Collections.singleton(saksnummerAnnenForelder)));
                pipRepository.hentAnnenPartForSaksnummer(saksnummerAnnenForelder)
                        .ifPresent(apaktør -> builder.leggTilRessurs(ForeldrepengerDataKeys.ANNENPART, apaktør));
                pipRepository.hentOppgittAleneomsorgForSaksnummer(saksnummerAnnenForelder)
                        .ifPresent(alene -> builder.leggTilRessurs(ForeldrepengerDataKeys.ALENEOMSORG, alene.toString()));
            });
        } else {
            builder.leggTilAktørIdSet(pipRepository.hentAktørIdForSaksnummer(dataAttributter.getVerdier(StandardAbacAttributtType.SAKSNUMMER)));
            builder.leggTilAktørIdSet(pipRepository.hentAktørIdForBehandling(dataAttributter.getVerdier(StandardAbacAttributtType.BEHANDLING_ID)));
            builder.leggTilAktørIdSet(pipRepository.hentAktørIdForForsendelseIder(dataAttributter.getVerdier(AppAbacAttributtType.FORSENDELSE_UUID)));
        }
        var appRessursData = builder.build();
        LOG.trace("Laget PDP app ressursdata OK {}", appRessursData);
        return appRessursData;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pipRepository=" + pipRepository + "]";
    }
}
