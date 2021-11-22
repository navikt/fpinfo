package no.nav.foreldrepenger.info.v2;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonGetter;

interface Sak {

    Saksnummer saksnummer();

    Familiehendelse familiehendelse();

    @SuppressWarnings("unused")
    boolean sakAvsluttet();

    Set<AktørId> barn();

    @SuppressWarnings("unused")
    @JsonGetter
    default boolean gjelderAdopsjon() {
        return familiehendelse().fødselsdato() == null && familiehendelse().termindato() == null;
    }
}
