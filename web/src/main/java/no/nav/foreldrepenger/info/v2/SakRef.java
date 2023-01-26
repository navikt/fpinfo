package no.nav.foreldrepenger.info.v2;

import java.util.Objects;

record SakRef(no.nav.foreldrepenger.info.v2.Saksnummer saksnummer,
              String fagsakStatus) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var sakRef = (SakRef) o;
        return saksnummer.equals(sakRef.saksnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksnummer);
    }

    @Override
    public String toString() {
        return "SakRef{" + "saksnummer=" + saksnummer + ", fagsakStatus='" + fagsakStatus + '\'' + '}';
    }
}
