package no.nav.foreldrepenger.info.v2;

import java.util.Set;

record SvpSak(Saksnummer saksnummer,
              boolean sakAvsluttet,
              Familiehendelse familiehendelse,
              Set<AktørId> barn) implements Sak {
}
