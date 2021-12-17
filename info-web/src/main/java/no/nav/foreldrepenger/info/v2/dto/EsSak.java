package no.nav.foreldrepenger.info.v2.dto;

import java.util.Set;

public record EsSak(Saksnummer saksnummer,
             Familiehendelse familiehendelse,
             EsVedtak gjeldendeVedtak,
             EsÅpenBehandling åpenBehandling,
             Set<PersonDetaljer> barn,
             boolean sakAvsluttet,
             boolean gjelderAdopsjon) {
}
