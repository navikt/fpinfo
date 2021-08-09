package no.nav.foreldrepenger.info.app.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.BehandlingÅrsak;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.app.ResourceLink;
import no.nav.foreldrepenger.info.app.tjenester.dto.AktørIdDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.SakDto;
import no.nav.foreldrepenger.info.datatyper.BehandlingType;
import no.nav.foreldrepenger.info.datatyper.BehandlingÅrsakType;
import no.nav.foreldrepenger.info.repository.Repository;

class SakTjenesteTest {

    @Test
    @Disabled
    public void skalReturnereLenkerTilBehandlinger() {
        var repository = mock(Repository.class);
        var tjeneste = new SakTjeneste(repository);

        var saksnummer = "123";
        var aktørId = "aktørId";
        var sak = Sak.builder().medAktørId(aktørId).medSaksnummer(saksnummer).build();
        when(repository.hentSak(aktørId)).thenReturn(List.of(sak));

        var førstegangsbehandling = førstegangsbehandling(1L);
        var revurderingEndringssøknad = revurdering(BehandlingÅrsakType.ENDRINGSSØKNAD, 2L);
        var revurderingAnnet = revurdering(BehandlingÅrsakType.ANNET, 3L);
        when(repository.hentTilknyttedeBehandlinger(saksnummer)).thenReturn(
                List.of(førstegangsbehandling, revurderingAnnet, revurderingEndringssøknad));

        var prefix = "link";
        var sakDtos = tjeneste.hentSak(new AktørIdDto(aktørId), prefix, "link2");

        assertThat(sakDtos).hasSize(1);
        assertThat(getBehandlingLinks(sakDtos)).hasSize(2);
        assertThat(getBehandlingLinks(sakDtos).map(l -> l.href().getPath())).containsExactlyInAnyOrder(
                prefix + førstegangsbehandling.getBehandlingId(), prefix + revurderingEndringssøknad.getBehandlingId());
    }

    private Behandling revurdering(BehandlingÅrsakType årsakType, Long behandlingId) {
        return Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingÅrsaker(List.of(new BehandlingÅrsak(årsakType)))
                .medBehandlingId(behandlingId)
                .build();
    }

    private Behandling førstegangsbehandling(Long behandlingId) {
        return Behandling.builder()
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .medBehandlingÅrsaker(List.of(new BehandlingÅrsak(BehandlingÅrsakType.ANNET)))
                .medBehandlingId(behandlingId)
                .build();
    }

    private Stream<ResourceLink> getBehandlingLinks(List<SakDto> sakDtos) {
        return sakDtos.get(0).getLenker().stream().filter(resourceLink -> resourceLink.rel().equals("behandlinger"));
    }

}
