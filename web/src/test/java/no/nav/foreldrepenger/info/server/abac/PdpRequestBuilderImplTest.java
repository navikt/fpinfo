package no.nav.foreldrepenger.info.server.abac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.info.server.abac.AppAbacAttributtType;
import no.nav.foreldrepenger.info.server.abac.PdpRequestBuilderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.info.repository.PipRepository;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.pdp.ForeldrepengerDataKeys;

@ExtendWith(MockitoExtension.class)
class PdpRequestBuilderImplTest {
    private static final Long BEHANDLINGS_ID = 123L;
    private static final String AKTØRID = "9900077";
    private static final String ANNEN_PART_ID = "7700099";
    private static final String SAKSNR = "678";
    private static final UUID FORSENDELSE_ID = UUID.randomUUID();
    @Mock
    private PipRepository pip;

    private PdpRequestBuilder requestBuilder;
    private AbacDataAttributter attributter;

    @BeforeEach
    void beforeEach() {
        requestBuilder = new PdpRequestBuilderImpl(pip);
        attributter = byggAbacAttributtSamling();

    }

    @Test
    void skal_utlede_aktørid_fra_behandlingid() {
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_ID, BEHANDLINGS_ID));
        when(pip.hentAktørIdForBehandling(Collections.singleton(BEHANDLINGS_ID)))
                .thenReturn(List.of(AKTØRID));

        var request = requestBuilder.lagAppRessursData(attributter);
        assertThat(request.getAktørIdSet()).containsOnly(AKTØRID);
    }

    @Test
    void skal_utlede_aktørid_fra_saksnummer() {
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.SAKSNUMMER, SAKSNR));

        when(pip.hentAktørIdForSaksnummer(Collections.singleton(SAKSNR)))
                .thenReturn(Collections.singletonList(AKTØRID));

        var request = requestBuilder.lagAppRessursData(attributter);
        assertThat(request.getAktørIdSet()).containsOnly(AKTØRID);
    }

    @Test
    void skal_legge_til_omsorg_og_annen_part() {
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(AppAbacAttributtType.ANNEN_PART, ANNEN_PART_ID));
        attributter.leggTil(AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.AKTØR_ID, AKTØRID));

        when(pip.hentAktørIdForSaksnummer(Collections.singleton(SAKSNR)))
                .thenReturn(List.of(AKTØRID));
        when(pip.finnSakenTilAnnenForelder(Collections.singleton(AKTØRID),
                Collections.singleton(ANNEN_PART_ID))).thenReturn(Optional.of(SAKSNR));
        when(pip.hentAnnenPartForSaksnummer(SAKSNR)).thenReturn(Optional.of(ANNEN_PART_ID));
        when(pip.hentOppgittAleneomsorgForSaksnummer(SAKSNR)).thenReturn(Optional.of(Boolean.TRUE));

        var request = requestBuilder.lagAppRessursData(attributter);
        assertThat(request.getAktørIdSet()).containsOnly(AKTØRID);
        assertThat(request.getResource(ForeldrepengerDataKeys.ANNENPART).verdi()).isEqualTo(ANNEN_PART_ID);
        assertThat(request.getResource(ForeldrepengerDataKeys.ALENEOMSORG).verdi()).isEqualTo("true");
    }

    private static AbacDataAttributter byggAbacAttributtSamling() {
        return AbacDataAttributter.opprett();
    }
}
