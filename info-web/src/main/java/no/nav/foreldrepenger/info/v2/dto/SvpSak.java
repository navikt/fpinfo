package no.nav.foreldrepenger.info.v2.dto;

import java.util.Set;

public record SvpSak(Saksnummer saksnummer,
              Familiehendelse familiehendelse,
              Set<PersonDetaljer> barn,
              boolean sakAvsluttet,
              boolean gjelderAdopsjon) {
}
