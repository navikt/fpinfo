package no.nav.foreldrepenger.info.datatyper;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public enum BehandlingType {

    FØRSTEGANGSBEHANDLING("BT-002"),
    REVURDERING("BT-004"),
    ANNET("ANNET") //Samler alle ukjente
    ;

    private final String kode;

    BehandlingType(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<BehandlingType, String> {

        @Override
        public BehandlingType convertToEntityAttribute(String dbData) {
            return Stream.of(values()).filter(e -> e.kode.equals(dbData)).findFirst().orElse(ANNET);
        }

        @Override
        public String convertToDatabaseColumn(BehandlingType behandlingType) {
            throw new IllegalStateException("Ikke støttet konvertering til db");
        }
    }
}
