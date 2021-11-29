package no.nav.foreldrepenger.info.v2;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

record FpSak(Saksnummer saksnummer,
             boolean sakAvsluttet,
             boolean kanSøkeOmEndring,
             boolean sakTilhørerMor,
             RettighetType rettighetType,
             AnnenPart annenPart,
             Familiehendelse familiehendelse,
             FpVedtak gjeldendeVedtak,
             FpÅpenBehandling åpenBehandling,
             @JsonIgnore LocalDateTime opprettetTidspunkt,
             Set<AktørId> barn,
             Dekningsgrad dekningsgrad) implements Sak {

}
