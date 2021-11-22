package no.nav.foreldrepenger.info.v2;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@JsonSerialize(using = SerializableValue.Serializer.class)
interface SerializableValue {

    Object value();

    class Serializer extends StdSerializer<SerializableValue> {

        Serializer(Class<SerializableValue> t) {
            super(t);
        }

        @SuppressWarnings("unused")
        Serializer() {
            this(SerializableValue.class);
        }

        @Override
        public void serialize(SerializableValue v,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeObject(v.value());
        }
    }
}
