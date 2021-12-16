package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;

record Familiehendelse(LocalDate fødselsdato,
                       LocalDate termindato,
                       int antallBarn,
                       LocalDate omsorgsovertakelse) {

    no.nav.foreldrepenger.info.v2.dto.Familiehendelse tilDto() {
        return new no.nav.foreldrepenger.info.v2.dto.Familiehendelse(fødselsdato, termindato, antallBarn, omsorgsovertakelse);
    }
}
