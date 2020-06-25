package no.nav.foreldrepenger.info.domene.converters;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import no.nav.foreldrepenger.info.felles.datatyper.GraderingAvslagÅrsak;

@Converter
public class StringToGraderingAvslagÅrsakConverter implements AttributeConverter<GraderingAvslagÅrsak, String> {

    public StringToGraderingAvslagÅrsakConverter() {
    }

    @Override
    public String convertToDatabaseColumn(GraderingAvslagÅrsak årsak) {
        return Optional.ofNullable(årsak)
                .map(GraderingAvslagÅrsak::getVerdi)
                .orElse(null);
    }

    @Override
    public GraderingAvslagÅrsak convertToEntityAttribute(String dbData) {
        return GraderingAvslagÅrsak.get(dbData);
    }
}
