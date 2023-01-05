package no.nav.foreldrepenger.info;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Prosent implements Comparable<Prosent>{

    public static final Prosent ZERO = new Prosent(BigDecimal.ZERO);

    private final BigDecimal verdi;

    public Prosent(BigDecimal verdi) {
        this.verdi = scale(verdi);
    }

    private BigDecimal scale(BigDecimal verdi) {
        return verdi.setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal decimalValue() {
        return scale(verdi);
    }

    public Long longValue() {
        return decimalValue().longValue();
    }

    @Override
    public String toString() {
        return verdi.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (Prosent) o;
        return Objects.equals(decimalValue(), that.decimalValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(decimalValue());
    }

    @Override
    public int compareTo(Prosent samtidigUttaksprosent) {
        return decimalValue().compareTo(samtidigUttaksprosent.decimalValue());
    }
}
