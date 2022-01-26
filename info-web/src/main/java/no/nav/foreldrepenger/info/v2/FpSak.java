package no.nav.foreldrepenger.info.v2;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

record FpSak(Saksnummer saksnummer,
             boolean sakAvsluttet,
             boolean kanSøkeOmEndring,
             boolean sakTilhørerMor,
             boolean gjelderAdopsjon,
             boolean morUføretrygd,
             RettighetType rettighetType,
             AnnenPart annenPart,
             Familiehendelse familiehendelse,
             FpVedtak gjeldendeVedtak,
             FpÅpenBehandling åpenBehandling,
             Set<AktørId> barn,
             Dekningsgrad dekningsgrad,
             LocalDateTime opprettetTidspunkt)  {

    no.nav.foreldrepenger.info.v2.dto.FpSak tilDto() {
        var annenPartDto = annenPart == null ? null : annenPart.tilDto();
        var gjeldendeVedtakDto = gjeldendeVedtak == null ? null : gjeldendeVedtak.tilDto();
        var familiehendelseDto = familiehendelse == null ? null : familiehendelse.tilDto();
        var åpenBehandlingDto = åpenBehandling == null ? null : åpenBehandling.tilDto();
        return new no.nav.foreldrepenger.info.v2.dto.FpSak(saksnummer.tilDto(), sakAvsluttet, kanSøkeOmEndring, sakTilhørerMor,
                gjelderAdopsjon, morUføretrygd, rettighetType.tilDto(), annenPartDto, familiehendelseDto, gjeldendeVedtakDto,
                åpenBehandlingDto, barn.stream().map(b -> b.tilDto()).collect(Collectors.toSet()), dekningsgrad.tilDto());
    }
}
