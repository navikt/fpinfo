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

class EsSakerTjenesteTest {

    @Test
    void ingen_es() {
        var repository = new InMemTestRepository();
        var tjeneste = new EsSakerTjeneste(repository);
        var aktørId = "000";
        assertThat(tjeneste.hentFor(new AktørId(aktørId))).isEmpty();
    }

    @Test
    void hent_es_adopsjon() {
        var repository = new InMemTestRepository();
        var tjeneste = new EsSakerTjeneste(repository);

        var aktørId = "000";

        var saksnummer = new no.nav.foreldrepenger.info.Saksnummer("123");
        var behandlingId = 345L;
        var omsorgsovertakelseDato = LocalDate.of(2023, 1, 24);
        var fødselsdato = omsorgsovertakelseDato.minusWeeks(1);

        repository.lagre(opprettetSak(aktørId, saksnummer, behandlingId));
        repository.lagre(åpenFørstegangsbehandling(saksnummer, behandlingId));
        repository.lagre(new FamilieHendelse(behandlingId, 1, null, fødselsdato, omsorgsovertakelseDato));
        repository.lagre(søknad(behandlingId, DokumentTypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON));

        var resultat = tjeneste.hentFor(new AktørId(aktørId));

        assertThat(resultat).hasSize(1);
        var esSak = resultat.stream().findFirst().orElseThrow();
        assertThat(esSak.sakAvsluttet()).isFalse();
        assertThat(esSak.familiehendelse()).isEqualTo(new Familiehendelse(fødselsdato, null, 1, omsorgsovertakelseDato));
        assertThat(esSak.åpenBehandling().tilstand()).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
    }

    @Test
    void hent_es_fødsel() {
        var repository = new InMemTestRepository();
        var tjeneste = new EsSakerTjeneste(repository);

        var aktørId = "000";

        var saksnummer = new no.nav.foreldrepenger.info.Saksnummer("123");
        var behandlingId = 345L;
        var termindato = LocalDate.of(2023, 1, 24);
        var fødselsdato = termindato.minusWeeks(1);

        repository.lagre(opprettetSak(aktørId, saksnummer, behandlingId));
        repository.lagre(åpenFørstegangsbehandling(saksnummer, behandlingId));
        repository.lagre(new FamilieHendelse(behandlingId, 2, termindato, fødselsdato, null));
        repository.lagre(søknad(behandlingId, DokumentTypeId.SØKNAD_FORELDREPENGER_FØDSEL));

        var resultat = tjeneste.hentFor(new AktørId(aktørId));

        assertThat(resultat).hasSize(1);
        var esSak = resultat.stream().findFirst().orElseThrow();
        assertThat(esSak.sakAvsluttet()).isFalse();
        assertThat(esSak.familiehendelse()).isEqualTo(new Familiehendelse(fødselsdato, termindato, 2, null));
        assertThat(esSak.åpenBehandling().tilstand()).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
    }

    private static List<MottattDokument> søknad(long behandlingId, DokumentTypeId dokumentTypeId) {
        return List.of(new MottattDokument.Builder()
                .medBehandlingId(behandlingId)
                .medType(dokumentTypeId)
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
                .medFagsakYtelseType(FagsakYtelseType.ES.name())
                .build();
    }
}
