package no.nav.foreldrepenger.info.v2;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.info.Behandling;
import no.nav.foreldrepenger.info.FamilieHendelse;
import no.nav.foreldrepenger.info.InMemTestRepository;
import no.nav.foreldrepenger.info.MottattDokument;
import no.nav.foreldrepenger.info.Sak;
import no.nav.foreldrepenger.info.datatyper.BehandlingType;
import no.nav.foreldrepenger.info.datatyper.DokumentTypeId;
import no.nav.foreldrepenger.info.datatyper.FagsakYtelseType;

class SvpSakerTjenesteTest {

    @Test
    void ingen_svp() {
        var repository = new InMemTestRepository();
        var tjeneste = new SvpSakerTjeneste(repository);
        var aktørId = "000";
        assertThat(tjeneste.hentFor(new AktørId(aktørId))).isEmpty();
    }

    @Test
    void hent_svp() {
        var repository = new InMemTestRepository();
        var tjeneste = new SvpSakerTjeneste(repository);

        var aktørId = "000";

        var saksnummer = new no.nav.foreldrepenger.info.Saksnummer("123");
        var behandlingId = 345L;
        var termindato = LocalDate.of(2023, 1, 24);

        repository.lagre(opprettetSak(aktørId, saksnummer, behandlingId));
        repository.lagre(åpenFørstegangsbehandling(saksnummer, behandlingId));
        repository.lagre(familieHendelse(behandlingId, termindato));
        repository.lagre(søknad(behandlingId));

        var resultat = tjeneste.hentFor(new AktørId(aktørId));

        assertThat(resultat).hasSize(1);
        var svpSak = resultat.stream().findFirst().orElseThrow();
        assertThat(svpSak.sakAvsluttet()).isFalse();
        assertThat(svpSak.familiehendelse()).isEqualTo(new Familiehendelse(null, termindato, 0, null));
        assertThat(svpSak.åpenBehandling().tilstand()).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
    }

    private static FamilieHendelse familieHendelse(long behandlingId, LocalDate termindato) {
        return new FamilieHendelse(behandlingId, 0, termindato, null, null);
    }

    private static List<MottattDokument> søknad(long behandlingId) {
        return List.of(new MottattDokument.Builder()
                .medBehandlingId(behandlingId)
                .medType(DokumentTypeId.SØKNAD_SVANGERSKAPSPENGER)
                .build());
    }

    private static Behandling åpenFørstegangsbehandling(no.nav.foreldrepenger.info.Saksnummer saksnummer,
                                                        long behandlingId) {
        return new Behandling.Builder()
                .medBehandlingId(behandlingId)
                .medBehandlingStatus("OPPR")
                .medSaksnummer(saksnummer)
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .build();
    }

    private static Sak opprettetSak(String aktørId, no.nav.foreldrepenger.info.Saksnummer saksnummer,
                                    long behandlingId) {
        return new Sak.Builder()
                .medSaksnummer(saksnummer)
                .medBehandlingId(String.valueOf(behandlingId))
                .medFagsakStatus("OPPR")
                .medAktørId(aktørId)
                .medFagsakYtelseType(FagsakYtelseType.SVP.name())
                .build();
    }
}
