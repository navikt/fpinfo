package no.nav.foreldrepenger.info.v2.dto;

import java.time.LocalDate;

public record Søknadsperiode(LocalDate fom, LocalDate tom, KontoType kontoType) {

}
