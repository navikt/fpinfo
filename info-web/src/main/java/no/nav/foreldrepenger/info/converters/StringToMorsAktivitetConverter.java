package no.nav.foreldrepenger.info.converters;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import no.nav.foreldrepenger.info.datatyper.MorsAktivitet;

@Converter
public class StringToMorsAktivitetConverter implements AttributeConverter<MorsAktivitet, String> {

    @Override
    public String convertToDatabaseColumn(MorsAktivitet attribute) {
        return Optional.ofNullable(attribute)
                .map(MorsAktivitet::getVerdi)
                .orElse(null);
    }

    @Override
    public MorsAktivitet convertToEntityAttribute(String dbData) {
        return MorsAktivitet.get(dbData);
    }
}
