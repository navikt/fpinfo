package no.nav.foreldrepenger.info;

import java.util.Objects;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity(name = "Aksjonspunkt")
@Table(name = "AKSJONSPUNKT")
@Immutable
public class Aksjonspunkt {

    @Id
    @Column(name = "ID")
    private long id;

    @Column(name = "BEHANDLING_ID")
    private long behandlingId;

    @Convert(converter = Status.KodeverdiConverter.class)
    @Column(name = "STATUS")
    private Status status;

    @Convert(converter = Definisjon.KodeverdiConverter.class)
    @Column(name = "DEFINISJON")
    private Definisjon definisjon;

    @Convert(converter = Venteårsak.KodeverdiConverter.class)
    @Column(name = "vent_aarsak")
    private Venteårsak venteårsak;

    protected Aksjonspunkt() {
    }

    public Definisjon getDefinisjon() {
        return definisjon;
    }

    public Status  getStatus() {
        return status;
    }

    public long getBehandlingId() {
        return behandlingId;
    }

    public Venteårsak getVenteårsak() {
        return venteårsak;
    }

    public static class Builder {
        private final Aksjonspunkt aksjonspunkt;

        public Builder() {
            this.aksjonspunkt = new Aksjonspunkt();
        }

        public Builder medBehandlingId(long behandlingId) {
            aksjonspunkt.behandlingId = behandlingId;
            return this;
        }

        public Builder medStatus(Status status) {
            aksjonspunkt.status = status;
            return this;
        }

        public Builder medDefinisjon(Definisjon definisjon) {
            aksjonspunkt.definisjon = definisjon;
            return this;
        }

        public Builder medVenteårsak(Venteårsak venteårsak) {
            aksjonspunkt.venteårsak = venteårsak;
            return this;
        }

        public Aksjonspunkt build() {
            return aksjonspunkt;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aksjonspunkt that = (Aksjonspunkt) o;
        return id == that.id && behandlingId == that.behandlingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, behandlingId);
    }

    @Override
    public String toString() {
        return "Aksjonspunkt{" + "id=" + id + ", behandlingId=" + behandlingId + ", status='" + status + '\'' + ", definisjon='" + definisjon + '\'' + '}';
    }

    public enum Definisjon {

        MANUELT_SATT_PÅ_VENT("7001"),
        VENT_PÅ_KOMPLETT_SØKNAD("7003"),
        VENT_PGA_FOR_TIDLIG_SØKNAD("7008"),
        VENT_PÅ_SISTE_AAP_ELLER_DP_MELDEKORT("7020"),
        VENT_ETTERLYST_INNTEKTSMELDING("7030"),
        ANNET(null),
        ;

        private final String kode;

        Definisjon(String kode) {
            this.kode = kode;
        }

        @Converter(autoApply = true)
        public static class KodeverdiConverter implements AttributeConverter<Definisjon, String> {

            @Override
            public Definisjon convertToEntityAttribute(String dbData) {
                return Stream.of(Definisjon.values()).filter(d -> Objects.equals(d.kode, dbData)).findFirst().orElse(ANNET);
            }

            @Override
            public String convertToDatabaseColumn(Definisjon status) {
                //Støtter bare en les fra db
                throw new IllegalStateException();
            }
        }
    }

    public enum Status {

        AVBRUTT("AVBR"),
        OPPRETTET("OPPR"),
        UTFØRT("UTFO")
        ;

        private final String kode;

        Status(String kode) {
            this.kode = kode;
        }

        @Converter(autoApply = true)
        public static class KodeverdiConverter implements AttributeConverter<Status, String> {

            @Override
            public Status convertToEntityAttribute(String dbData) {
                return Stream.of(Status.values()).filter(d -> Objects.equals(d.kode, dbData)).findFirst().orElse(null);
            }

            @Override
            public String convertToDatabaseColumn(Status status) {
                //Støtter bare en les fra db
                throw new IllegalStateException();
            }
        }
    }

    public enum Venteårsak {
        AVV_DOK, VENT_OPDT_INNTEKTSMELDING, VENT_PÅ_SISTE_AAP_MELDEKORT
        ;

        @Converter(autoApply = true)
        public static class KodeverdiConverter implements AttributeConverter<Venteårsak, String> {

            @Override
            public Venteårsak convertToEntityAttribute(String dbData) {
                return Stream.of(Venteårsak.values()).filter(d -> Objects.equals(d.name(), dbData)).findFirst().orElse(null);
            }

            @Override
            public String convertToDatabaseColumn(Venteårsak status) {
                //Støtter bare en les fra db
                throw new IllegalStateException();
            }
        }
    }
}
