package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

record FpSak(Saksnummer saksnummer,
             boolean sakAvsluttet,
             LocalDate sisteSøknadMottattDato,
             boolean kanSøkeOmEndring,
             boolean sakTilhørerMor,
             boolean morUføretrygd,
             boolean harAnnenForelderTilsvarendeRettEØS,
             boolean ønskerJustertUttakVedFødsel,
             RettighetType rettighetType,
             AnnenPart annenPart,
             Familiehendelse familiehendelse,
             FpVedtak gjeldendeVedtak,
             FpÅpenBehandling åpenBehandling,
             Set<AktørId> barn,
             Dekningsgrad dekningsgrad,
             LocalDateTime opprettetTidspunkt)  {

    no.nav.foreldrepenger.common.innsyn.FpSak tilDto() {
        var annenPartDto = annenPart == null ? null : annenPart.tilDto();
        var gjeldendeVedtakDto = gjeldendeVedtak == null ? null : gjeldendeVedtak.tilDto();
        var familiehendelseDto = familiehendelse == null ? null : familiehendelse.tilDto();
        var åpenBehandlingDto = åpenBehandling == null ? null : åpenBehandling.tilDto();
        var gjelderAdopsjon = familiehendelse != null && familiehendelse.gjelderAdopsjon();
        return new no.nav.foreldrepenger.common.innsyn.FpSak(saksnummer.tilDto(), sakAvsluttet, sisteSøknadMottattDato,
                kanSøkeOmEndring, sakTilhørerMor, gjelderAdopsjon, morUføretrygd,
                harAnnenForelderTilsvarendeRettEØS, ønskerJustertUttakVedFødsel,
                rettighetType.tilDto(), annenPartDto, familiehendelseDto, gjeldendeVedtakDto,
                åpenBehandlingDto, barn.stream().map(AktørId::tilDto).collect(Collectors.toSet()), dekningsgrad.tilDto());
    }
}
