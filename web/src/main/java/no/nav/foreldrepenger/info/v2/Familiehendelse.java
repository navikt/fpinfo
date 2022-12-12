package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

record Familiehendelse(LocalDate fødselsdato,
                       LocalDate termindato,
                       int antallBarn,
                       LocalDate omsorgsovertakelse) {

    no.nav.foreldrepenger.common.innsyn.v2.Familiehendelse tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.v2.Familiehendelse(fødselsdato, termindato, antallBarn, omsorgsovertakelse);
    }

    @JsonIgnore
    public LocalDate familiehendelse() {
        if (omsorgsovertakelse() != null) {
            return omsorgsovertakelse();
        }
        if (fødselsdato() != null) {
            return fødselsdato();
        }
        return termindato();
    }
}
