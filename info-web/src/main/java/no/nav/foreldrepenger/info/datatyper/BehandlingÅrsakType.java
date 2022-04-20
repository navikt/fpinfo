package no.nav.foreldrepenger.info.datatyper;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public enum BehandlingÅrsakType {

    ENDRINGSSØKNAD("RE-END-FRA-BRUKER"),
    ANNET("ANNET") //Samler alle ukjente/urelevante
    ;

    private final String kode;

    BehandlingÅrsakType(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<BehandlingÅrsakType, String> {

        @Override
        public BehandlingÅrsakType convertToEntityAttribute(String dbData) {
            return Stream.of(values()).filter(e -> e.kode.equals(dbData)).findFirst().orElse(ANNET);
        }

        @Override
        public String convertToDatabaseColumn(BehandlingÅrsakType behandlingType) {
            throw new IllegalStateException("Ikke støttet konvertering til db");
        }
    }
}
