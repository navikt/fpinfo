package no.nav.foreldrepenger.info.v2;

import java.util.Set;

record EsSak(Saksnummer saksnummer,
             boolean sakAvsluttet,
             Familiehendelse familiehendelse,
             EsVedtak gjeldendeVedtak,
             EsÅpenBehandling åpenBehandling,
             Set<AktørId> barn) implements Sak {
}
