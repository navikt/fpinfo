package no.nav.foreldrepenger.info.v2;

record SvpSak(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer,
              Familiehendelse familiehendelse,
              boolean sakAvsluttet,
              SvpÅpenBehandling åpenBehandling)  {

    no.nav.foreldrepenger.common.innsyn.v2.SvpSak tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.SvpSak(saksnummer.tilDto(),
                familiehendelse.tilDto(), sakAvsluttet, åpenBehandling == null ? null : åpenBehandling.tilDto());
    }
}
