package no.nav.foreldrepenger.info.tjenester;

import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.info.tjenester.dto.AktørAnnenPartDto;
import no.nav.foreldrepenger.info.tjenester.dto.AktørIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.BehandlingDto;
import no.nav.foreldrepenger.info.tjenester.dto.BehandlingIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseIdDto;
import no.nav.foreldrepenger.info.tjenester.dto.ForsendelseStatusDataDTO;
import no.nav.foreldrepenger.info.tjenester.dto.SakDto;
import no.nav.foreldrepenger.info.tjenester.dto.SaksnummerDto;
import no.nav.foreldrepenger.info.tjenester.dto.SøknadXmlDto;
import no.nav.foreldrepenger.info.tjenester.dto.SøknadsGrunnlagDto;

public interface DokumentforsendelseTjeneste {

    BehandlingDto hentBehandling(BehandlingIdDto behandlingId, String linkPathSøknad);

    List<SakDto> hentSakStatus(AktørIdDto aktørIdDto, String linkPathBehandling, String linkPathUttaksplan);

    Optional<SøknadXmlDto> hentSøknadXml(BehandlingIdDto behandlingId);

    Optional<ForsendelseStatusDataDTO> hentStatusInformasjon(ForsendelseIdDto forsendelseIdDto);

    Optional<SøknadsGrunnlagDto> hentSøknadsgrunnlag(SaksnummerDto saksnummerDto, boolean erAnnenPart);

    Optional<SøknadsGrunnlagDto> hentSøknadAnnenPart(AktørIdDto aktørIdBrukerDto, AktørAnnenPartDto aktørAnnenPartDto);
}
