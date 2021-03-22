package io.cstool.comply22.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.SneakyThrows;
import org.neo4j.driver.internal.value.IntegerValue;

import java.io.IOException;
import java.util.Map;

public class DynPropsSerializer extends JsonSerializer<Map<String, Object>> {

    @Override
    public void serialize(Map<String,Object> map, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        map.forEach((key, value) -> {
            if (value instanceof IntegerValue)
                    writeNumber(gen, (IntegerValue) value);
            else {
                try {
                    gen.writeString(value.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @SneakyThrows
    private void writeNumber(JsonGenerator gen, IntegerValue value){
        gen.writeNumber(value.asInt());
    }
}
