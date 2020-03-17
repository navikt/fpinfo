package no.nav.foreldrepenger.info.domene;

import java.util.Objects;

public class Saksnummer {
    private final String saksnummer;

    public Saksnummer(String saksnummer) {
        Objects.requireNonNull(saksnummer);
        this.saksnummer = saksnummer;
    }

    public String asString() {
        return saksnummer;
    }

    @Override
    public String toString() {
        return "saksnummer " + saksnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Saksnummer that = (Saksnummer) o;
        return saksnummer != null ? saksnummer.equals(that.saksnummer) : that.saksnummer == null;
    }

    @Override
    public int hashCode() {
        return saksnummer != null ? saksnummer.hashCode() : 0;
    }
}
