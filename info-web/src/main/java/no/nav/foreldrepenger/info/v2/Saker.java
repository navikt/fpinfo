package no.nav.foreldrepenger.info.v2;

import java.util.Set;

record Saker(Set<FpSak> foreldrepenger,
             Set<EsSak> engangsstønad,
             Set<SvpSak> svangerskapspenger) {
}
