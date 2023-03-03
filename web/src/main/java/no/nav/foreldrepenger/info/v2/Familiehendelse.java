package no.nav.foreldrepenger.info.v2;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

record Familiehendelse(LocalDate fødselsdato,
                       LocalDate termindato,
                       int antallBarn,
                       LocalDate omsorgsovertakelse) {

    no.nav.foreldrepenger.common.innsyn.Familiehendelse tilDto() {
        return new no.nav.foreldrepenger.common.innsyn.Familiehendelse(fødselsdato, termindato, antallBarn, omsorgsovertakelse);
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

    public boolean gjelderAdopsjon() {
        return omsorgsovertakelse != null;
    }
}
