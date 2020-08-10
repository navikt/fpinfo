package no.nav.foreldrepenger.info.web.server.abac;

import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import no.nav.foreldrepenger.info.abac.AppAbacAttributtType;
import no.nav.foreldrepenger.info.pip.PipRepository;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;

public class PdpRequestBuilderImplTest {
    private static final String DUMMY_ID_TOKEN = "dummyheader.dymmypayload.dummysignaturee";
    private static final Long BEHANDLINGS_ID = 123L;
    private static final String AKTØR_ID = "9900077";
    private static final String ANNEN_PART_ID = "7700099";
    private static final String SAKSNUMMER = "678";
    private static final UUID FORSENDELSE_ID = UUID.randomUUID();
    private PipRepository pipRepositoryMock = Mockito.mock(PipRepository.class);

    private PdpRequestBuilderImpl requestBuilder = new PdpRequestBuilderImpl(pipRepositoryMock);

    @Test
    public void skal_utlede_aktørid_fra_behandlingid() {
        AbacAttributtSamling attributter = byggAbacAttributtSamling();
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.BEHANDLING_ID, BEHANDLINGS_ID));

        when(pipRepositoryMock.hentAktørIdForBehandling(Collections.singleton(BEHANDLINGS_ID)))
                .thenReturn(Collections.singletonList(AKTØR_ID));

        PdpRequest request = requestBuilder.lagPdpRequest(attributter);
        assertThat(request.getListOfString(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE)).containsOnly(AKTØR_ID);
    }

    @Test
    public void skal_utlede_aktørid_fra_saksnummer() {
        AbacAttributtSamling attributter = byggAbacAttributtSamling();
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.SAKSNUMMER, SAKSNUMMER));

        when(pipRepositoryMock.hentAktørIdForSaksnummer(Collections.singleton(SAKSNUMMER)))
                .thenReturn(Collections.singletonList(AKTØR_ID));

        PdpRequest request = requestBuilder.lagPdpRequest(attributter);
        assertThat(request.getListOfString(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE)).containsOnly(AKTØR_ID);
    }

    @Test
    public void skal_utlede_aktørid_fra_forsendelseId() {
        AbacAttributtSamling attributter = byggAbacAttributtSamling();
        attributter
                .leggTil(AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.FORSENDELSE_UUID, FORSENDELSE_ID));

        when(pipRepositoryMock.hentAktørIdForForsendelseIder(Collections.singleton(FORSENDELSE_ID)))
                .thenReturn(Collections.singletonList(AKTØR_ID));

        PdpRequest request = requestBuilder.lagPdpRequest(attributter);
        assertThat(request.getListOfString(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE)).containsOnly(AKTØR_ID);
    }

    @Test
    public void skal_legge_til_omsorg_og_annen_part() {
        AbacAttributtSamling attributter = byggAbacAttributtSamling();
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.ANNEN_PART, ANNEN_PART_ID));
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.AKTØR_ID, AKTØR_ID));

        when(pipRepositoryMock.hentAktørIdForSaksnummer(Collections.singleton(SAKSNUMMER)))
                .thenReturn(Collections.singletonList(AKTØR_ID));
        when(pipRepositoryMock.finnSaksnummerTilAnnenpart(Collections.singleton(AKTØR_ID),
                Collections.singleton(ANNEN_PART_ID))).thenReturn(Optional.of(SAKSNUMMER));
        when(pipRepositoryMock.hentAnnenPartForSaksnummer(SAKSNUMMER)).thenReturn(Optional.of(ANNEN_PART_ID));
        when(pipRepositoryMock.erAleneomsorg(SAKSNUMMER)).thenReturn(Optional.of(Boolean.TRUE));

        PdpRequest request = requestBuilder.lagPdpRequest(attributter);
        assertThat(request.getListOfString(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE)).containsOnly(AKTØR_ID);
        assertThat(request.getString(AppAbacAttributtType.RESOURCE_FORELDREPENGER_ANNEN_PART)).isEqualTo(ANNEN_PART_ID);
        assertThat(request.getOptional(AppAbacAttributtType.RESOURCE_FORELDREPENGER_ALENEOMSORG)).hasValue("true");
    }

    private static AbacAttributtSamling byggAbacAttributtSamling() {
        AbacAttributtSamling attributtSamling = AbacAttributtSamling.medJwtToken(DUMMY_ID_TOKEN);
        attributtSamling.setActionType(BeskyttetRessursActionAttributt.READ);
        attributtSamling.setResource(BeskyttetRessursResourceAttributt.FAGSAK.getEksternKode());
        return attributtSamling;
    }
}
