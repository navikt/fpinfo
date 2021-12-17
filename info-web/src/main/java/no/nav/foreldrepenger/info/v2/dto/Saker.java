package no.nav.foreldrepenger.info.v2.dto;

import java.util.Set;

public record Saker(Set<FpSak> foreldrepenger, Set<EsSak> engangsstønad, Set<SvpSak> svangerskapspenger) {
}
