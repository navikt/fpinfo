package no.nav.foreldrepenger.info.web.app.jackson;

import javax.validation.constraints.Pattern;

class Patternklasse {

    @Pattern(regexp = "[Aa]")
    private String fritekst;
}
