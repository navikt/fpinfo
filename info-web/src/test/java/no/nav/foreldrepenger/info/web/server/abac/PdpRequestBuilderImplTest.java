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
    private static final String DUMMY_ID_TOKEN = "eyJraWQiOiI3Mzk2ZGIyZC1hN2MyLTQ1OGEtYjkzNC02ODNiNDgzYzUyNDIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiRzJ1Zl83OW1TTUhHSWFfNjFxTnJfUSIsInN1YiI6IjA5MDg4NDIwNjcyIiwidmVyIjoiMS4wIiwiaXNzIjoiaHR0cHM6XC9cL3Rva2VuZGluZ3MuZGV2LWdjcC5uYWlzLmlvIiwibm9uY2UiOiJWR1dyS1Zsa3RXZ3hCdTlMZnNnMHliMmdMUVhoOHRaZHRaVTJBdWdPZVl3IiwiY2xpZW50X2lkIjoiZGV2LWZzczp0ZWFtZm9yZWxkcmVwZW5nZXI6ZnBzb2tuYWQtbW90dGFrIiwiYXVkIjoiZGV2LWZzczp0ZWFtZm9yZWxkcmVwZW5nZXI6ZnBpbmZvIiwiYWNyIjoiTGV2ZWw0IiwibmJmIjoxNjE2Njg1NDA0LCJpZHAiOiJodHRwczpcL1wvbmF2dGVzdGIyYy5iMmNsb2dpbi5jb21cL2QzOGYyNWFhLWVhYjgtNGM1MC05ZjI4LWViZjkyYzEyNTZmMlwvdjIuMFwvIiwiYXV0aF90aW1lIjoxNjE2Njg1NDAyLCJleHAiOjE2MTY2ODU3MDQsImlhdCI6MTYxNjY4NTQwNCwianRpIjoiNGMwNzBmMGUtNzI0Ny00ZTdjLWE1OWEtYzk2Yjk0NWMxZWZhIn0.OvzjuabvPHG9nlRVc_KlCUTHOdfeT9GtBkASUGIoMayWGeIBDkr4-jc9gu6uT_WQqi9IJnvPkWgP3veqYHcOHpapD1yVNaQpxlrJQ04yP6N3gvkn-DcrBRDb3II_6qSaPQ_us2PJBDPq2VD5TGrNOL6EFwr8FK3zglYr-PgjW016ULTcmx_7gdHmbiC5PEn1_OtGNxzoUhSGKoD3YtUWP0qdsXzoKyeFL5FG9uZMSrDHHiJBZQFXGL9OzBU49Zb2K-iEPqa9m91O2JZGkhebfLjCAIPLPN4J68GFyfTvtNkZO71znorjo-e1nWxz53Wkj---RDY3JlIqNqzqHTfJgQ";
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
