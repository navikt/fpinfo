package no.nav.foreldrepenger.info.v2;

record EsSak(Saksnummer saksnummer,
             Familiehendelse familiehendelse,
             boolean sakAvsluttet,
             EsÅpenBehandling åpenBehandling)  {

    no.nav.foreldrepenger.common.innsyn.v2.EsSak tilDto() {
        var åpenBehandlingDto = åpenBehandling == null ? null : åpenBehandling.tilDto();
        var familiehendelseDto = familiehendelse == null ? null : familiehendelse.tilDto();
        var gjelderAdopsjon = familiehendelse != null && familiehendelse.gjelderAdopsjon();
        return new no.nav.foreldrepenger.common.innsyn.v2.EsSak(saksnummer.tilDto(),
                familiehendelseDto, sakAvsluttet, åpenBehandlingDto, gjelderAdopsjon);
    }
}
