package no.nav.foreldrepenger.info.domene;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity(name = "FagsakRelasjon")
@Table(name = "FAGSAK_RELASJON")
@Immutable
public class FagsakRelasjon {

    @Id
    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    @Column(name = "FAGSAK_ID")
    private Long fagsakId;

    @Column(name = "SAKSNUMMER_EN")
    private String saksnummerEn;

    @Column(name = "SAKSNUMMER_TO")
    private String saksnummerTo;

    @Column(name = "ENDRET_TID")
    private LocalDateTime endretTidspunkt;

    public Saksnummer getSaksnummer() {
        return new Saksnummer(saksnummer);
    }

    public Long getFagsakId() {
        return fagsakId;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }

    public Optional<Saksnummer> finnSaksnummerAnnenpart() {
        return saksnummerFra(saksnummer.equals(saksnummerEn) ? saksnummerTo : saksnummerEn);
    }

    private static Optional<Saksnummer> saksnummerFra(String saksnummer) {
        return saksnummer == null ? Optional.empty() : Optional.of(new Saksnummer(saksnummer));
    }

}
