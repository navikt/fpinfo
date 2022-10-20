package no.nav.foreldrepenger.info.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

record Familiehendelse(LocalDate fødselsdato,
                       LocalDate termindato,
                       int antallBarn,
                       LocalDate omsorgsovertakelse) {

    no.nav.foreldrepenger.common.innsyn.v2.Familiehendelse tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.Familiehendelse(fødselsdato, termindato, antallBarn, omsorgsovertakelse);
    }

    @JsonIgnore
    public LocalDate familiehendelse() {
        if (fødselsdato() != null) {
            return fødselsdato();
        }
        if (termindato() != null) {
            return termindato();
        }
        return omsorgsovertakelse();
    }
}
