package no.nav.foreldrepenger.info.web.server.abac;

import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType.AKTØR_ID;
import static no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType.BEHANDLING_ID;
import static no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType.SAKSNUMMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.info.pip.PipRepository;
import no.nav.foreldrepenger.info.web.abac.AppAbacAttributtType;
import no.nav.foreldrepenger.info.web.abac.BeskyttetRessursAttributt;
import no.nav.foreldrepenger.info.web.abac.PdpRequestBuilderImpl;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacIdToken.TokenType;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;

@ExtendWith(MockitoExtension.class)
class PdpRequestBuilderImplTest {
    private static final String DUMMY_ID_TOKEN = "dummyheader.dymmypayload.dummysignaturee";
    private static final Long BEHANDLINGS_ID = 123L;
    private static final String AKTØRID = "9900077";
    private static final String ANNEN_PART_ID = "7700099";
    private static final String SAKSNR = "678";
    private static final UUID FORSENDELSE_ID = UUID.randomUUID();
    @Mock
    private PipRepository pip;
    private PdpRequestBuilder requestBuilder;
    private AbacAttributtSamling attributter;

    @BeforeEach
    void beforeEach() {
        requestBuilder = new PdpRequestBuilderImpl(pip);
        attributter = byggAbacAttributtSamling();
    }

    @Test
    void skal_utlede_aktørid_fra_behandlingid() {
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(BEHANDLING_ID, BEHANDLINGS_ID));
        when(pip.hentAktørIdForBehandling(Collections.singleton(BEHANDLINGS_ID)))
                .thenReturn(List.of(AKTØRID));

        var request = requestBuilder.lagPdpRequest(attributter);
        assertThat(request.getListOfString(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE)).containsOnly(AKTØRID);
    }

    @Test
    void skal_utlede_aktørid_fra_saksnummer() {
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(SAKSNUMMER, SAKSNR));

        when(pip.hentAktørIdForSaksnummer(Collections.singleton(SAKSNR)))
                .thenReturn(Collections.singletonList(AKTØRID));

        var request = requestBuilder.lagPdpRequest(attributter);
        assertThat(request.getListOfString(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE)).containsOnly(AKTØRID);
    }

    @Test
    void skal_utlede_aktørid_fra_forsendelseId() {
        attributter
                .leggTil(AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.FORSENDELSE_UUID, FORSENDELSE_ID));

        when(pip.hentAktørIdForForsendelseIder(Collections.singleton(FORSENDELSE_ID)))
                .thenReturn(Collections.singletonList(AKTØRID));

        var request = requestBuilder.lagPdpRequest(attributter);
        assertThat(request.getListOfString(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE)).containsOnly(AKTØRID);
    }

    @Test
    void skal_legge_til_omsorg_og_annen_part() {
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.ANNEN_PART, ANNEN_PART_ID));
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(AKTØR_ID, AKTØRID));

        when(pip.hentAktørIdForSaksnummer(Collections.singleton(SAKSNR)))
                .thenReturn(List.of(AKTØRID));
        when(pip.finnSakenTilAnnenForelder(Collections.singleton(AKTØRID),
                Collections.singleton(ANNEN_PART_ID))).thenReturn(Optional.of(SAKSNR));
        when(pip.hentAnnenPartForSaksnummer(SAKSNR)).thenReturn(Optional.of(ANNEN_PART_ID));
        when(pip.hentOppgittAleneomsorgForSaksnummer(SAKSNR)).thenReturn(Optional.of(Boolean.TRUE));

        PdpRequest request = requestBuilder.lagPdpRequest(attributter);
        assertThat(request.getListOfString(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE)).containsOnly(AKTØRID);
        assertThat(request.getString(AppAbacAttributtType.RESOURCE_FORELDREPENGER_ANNEN_PART)).isEqualTo(ANNEN_PART_ID);
        assertThat(request.getOptional(AppAbacAttributtType.RESOURCE_FORELDREPENGER_ALENEOMSORG)).hasValue("true");
    }

    private static AbacAttributtSamling byggAbacAttributtSamling() {
        var s = AbacAttributtSamling.medJwtToken(DUMMY_ID_TOKEN, TokenType.TOKENX);
        s.setActionType(BeskyttetRessursActionAttributt.READ);
        s.setResource(BeskyttetRessursAttributt.FAGSAK);
        return s;
    }
}
