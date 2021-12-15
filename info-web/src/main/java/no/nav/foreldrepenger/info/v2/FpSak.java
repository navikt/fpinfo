package no.nav.foreldrepenger.info.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Set;

record FpSak(Saksnummer saksnummer,
             boolean sakAvsluttet,
             boolean kanSøkeOmEndring,
             boolean sakTilhørerMor,
             boolean gjelderAdopsjon,
             RettighetType rettighetType,
             AnnenPart annenPart,
             Familiehendelse familiehendelse,
             FpVedtak gjeldendeVedtak,
             FpÅpenBehandling åpenBehandling,
             Set<PersonDetaljer> barn,
             Dekningsgrad dekningsgrad,
             @JsonIgnore LocalDateTime opprettetTidspunkt) implements Sak {
}
