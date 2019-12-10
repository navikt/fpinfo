package no.nav.foreldrepenger.info.domene;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity(name = "GjeldendeVedtattBehandling")
@Table(name = "GJELDENDE_VEDTATT_BEHANDLING")
@Immutable
public class GjeldendeVedtattBehandling {
    @Id
    @Column(name = "BEHANDLING_ID")
    private Long behandlingId;

    @Column(name = "SAKSNUMMER")
    private String saksnummer;

    public GjeldendeVedtattBehandling() {
        // Hibernate
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public Saksnummer getSaksnummer() {
        return new Saksnummer(saksnummer);
    }
}

