package no.nav.foreldrepenger.info.app.tjenester;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.foreldrepenger.info.Saksnummer;
import no.nav.foreldrepenger.info.app.tjenester.dto.AktørAnnenPartDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.AktørIdDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.SaksnummerDto;
import no.nav.foreldrepenger.info.app.tjenester.dto.SøknadsGrunnlagDto;
import no.nav.foreldrepenger.info.repository.Repository;

@Dependent
public class SøknadsGrunnlagTjeneste {

    private final BehandlingTjeneste behandlingTjeneste;
    private final UttakTjeneste uttakTjeneste;
    private final Repository repository;

    @Inject
    public SøknadsGrunnlagTjeneste(BehandlingTjeneste behandlingTjeneste,
            UttakTjeneste uttakTjeneste,
            Repository repository) {
        this.behandlingTjeneste = behandlingTjeneste;
        this.uttakTjeneste = uttakTjeneste;
        this.repository = repository;
    }

    public Optional<SøknadsGrunnlagDto> hentSøknadsgrunnlag(SaksnummerDto saksnummerDto, boolean erAnnenPart) {
        var saksnummer = new Saksnummer(saksnummerDto.saksnummer());
        return behandlingTjeneste.getGjeldendeBehandlingId(saksnummer)
                .flatMap(repository::hentSøknadsGrunnlag)
                .map(sg -> {
                    var fellesPlan = uttakTjeneste.hentFellesUttaksplan(saksnummer, erAnnenPart);
                    return SøknadsGrunnlagDto.fraDomene(saksnummer, sg).medUttaksPerioder(fellesPlan);
                });
    }

    public Optional<SøknadsGrunnlagDto> hentSøknadAnnenPart(AktørIdDto aktørIdBrukerDto,
            AktørAnnenPartDto aktørAnnenPartDto) {
        var sakAnnenPart = repository.finnNyesteSakForAnnenPart(aktørIdBrukerDto.aktørId(),
                aktørAnnenPartDto.annenPartAktørId());
        var søknadsgrunnlag = sakAnnenPart.flatMap(
                sap -> hentSøknadsgrunnlag(new SaksnummerDto(sap.getSaksnummer()), true));
        søknadsgrunnlag.ifPresent(sg -> sg.setAnnenPartFraSak(sakAnnenPart.get().getAktørIdAnnenPart()));
        return søknadsgrunnlag;
    }
}
