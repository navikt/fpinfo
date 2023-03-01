package no.nav.foreldrepenger.info;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity(name = "FamilieHendelse")
@Table(name = "FAMILIE_HENDELSE")
@Immutable
public class FamilieHendelse {

    @Id
    @Column(name = "BEHANDLING_ID")
    private long behandlingId;

    @Column(name = "ANTALL_BARN")
    private int antallBarn;

    @Column(name = "OMSORGSOVERTAKELSE_DATO")
    private LocalDate omsorgsovertakelseDato;

    @Column(name = "TERMINDATO")
    private LocalDate termindato;

    @Column(name = "FOEDSEL_DATO")
    private LocalDate fødselsdato;

    public FamilieHendelse(long behandlingId,
                           int antallBarn,
                           LocalDate termindato,
                           LocalDate fødselsdato,
                           LocalDate omsorgsovertakelseDato) {
        this.behandlingId = behandlingId;
        this.antallBarn = antallBarn;
        this.omsorgsovertakelseDato = omsorgsovertakelseDato;
        this.termindato = termindato;
        this.fødselsdato = fødselsdato;
    }

    FamilieHendelse() {

    }

    public long getBehandlingId() {
        return behandlingId;
    }

    public LocalDate getOmsorgsovertakelseDato() {
        return omsorgsovertakelseDato;
    }

    public LocalDate getTermindato() {
        return termindato;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FamilieHendelse that = (FamilieHendelse) o;
        return behandlingId == that.behandlingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId);
    }


    @Override
    public String toString() {
        return "FamilieHendelse{" + "behandlingId=" + behandlingId + ", antallBarn=" + antallBarn + ", omsorgsovertakelseDato="
            + omsorgsovertakelseDato + ", termindato=" + termindato + ", fødselsdato=" + fødselsdato + '}';
    }
}
