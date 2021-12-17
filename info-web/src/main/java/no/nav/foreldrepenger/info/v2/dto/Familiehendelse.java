package no.nav.foreldrepenger.info.v2.dto;

import java.time.LocalDate;

public record Familiehendelse(LocalDate fødselsdato,
                              LocalDate termindato,
                              int antallBarn,
                              LocalDate omsorgsovertakelse) {
}
