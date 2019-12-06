package no.nav.foreldrepenger.info.domene.converters;

import no.nav.foreldrepenger.info.felles.datatyper.MorsAktivitet;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

@Converter
public class StringToMorsAktivitetConverter implements AttributeConverter<MorsAktivitet, String> {

    public StringToMorsAktivitetConverter() {
    }

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
