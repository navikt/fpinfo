package no.nav.foreldrepenger.info.v2;

record SvpSak(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer,
              Familiehendelse familiehendelse,
              boolean sakAvsluttet,
              SvpÅpenBehandling åpenBehandling)  {

    no.nav.foreldrepenger.common.innsyn.SvpSak tilDto() {
        var familiehendelseDto = familiehendelse == null ? null : familiehendelse.tilDto();
        var åpenBehandlingDto = åpenBehandling == null ? null : åpenBehandling.tilDto();
        return new no.nav.foreldrepenger.common.innsyn.SvpSak(saksnummer.tilDto(),
                familiehendelseDto, sakAvsluttet, åpenBehandlingDto);
    }
}
