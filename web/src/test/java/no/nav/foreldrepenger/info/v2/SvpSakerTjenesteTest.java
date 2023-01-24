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
import no.nav.foreldrepenger.info.datatyper.FamilieHendelseType;

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

        var familiehendelseType = FamilieHendelseType.TERMIN.getVerdi();
        repository.lagre(opprettetSak(aktørId, saksnummer, behandlingId, familiehendelseType));
        repository.lagre(åpenFørstegangsbehandling(saksnummer, behandlingId, familiehendelseType));
        repository.lagre(getFamilieHendelse(behandlingId, termindato, familiehendelseType));
        repository.lagre(søknad(behandlingId));

        var resultat = tjeneste.hentFor(new AktørId(aktørId));

        assertThat(resultat).hasSize(1);
        var svpSak = resultat.stream().findFirst().orElseThrow();
        assertThat(svpSak.sakAvsluttet()).isFalse();
        assertThat(svpSak.familiehendelse()).isEqualTo(new Familiehendelse(null, termindato, 0, null));
        assertThat(svpSak.åpenBehandling().tilstand()).isEqualTo(BehandlingTilstand.UNDER_BEHANDLING);
    }

    private static FamilieHendelse getFamilieHendelse(long behandlingId, LocalDate termindato, String familiehendelseType) {
        return new FamilieHendelse(behandlingId, 0, familiehendelseType, null, termindato, null);
    }

    private static List<MottattDokument> søknad(long behandlingId) {
        return List.of(new MottattDokument.Builder()
                .medBehandlingId(behandlingId)
                .medType(DokumentTypeId.SØKNAD_SVANGERSKAPSPENGER)
                .build());
    }

    private static Behandling åpenFørstegangsbehandling(no.nav.foreldrepenger.info.Saksnummer saksnummer, long behandlingId, String familiehendelseType) {
        return new Behandling.Builder()
                .medBehandlingId(behandlingId)
                .medBehandlingStatus("OPPR")
                .medFamilieHendelseType(familiehendelseType)
                .medSaksnummer(saksnummer)
                .medBehandlingType(BehandlingType.FØRSTEGANGSBEHANDLING)
                .build();
    }

    private static Sak opprettetSak(String aktørId, no.nav.foreldrepenger.info.Saksnummer saksnummer, long behandlingId, String familiehendelseType) {
        return new Sak.Builder()
                .medSaksnummer(saksnummer)
                .medBehandlingId(String.valueOf(behandlingId))
                .medFagsakStatus("OPPR")
                .medAktørId(aktørId)
                .medFamilieHendelseType(familiehendelseType)
                .medFagsakYtelseType(FagsakYtelseType.SVP.name())
                .build();
    }
}