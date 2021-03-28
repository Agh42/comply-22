package io.cstool.comply22.entity;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.neo4j.driver.internal.value.*;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.neo4j.core.schema.CompositeProperty;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

@Node("Version")
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EntityVersion {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue()
    private Long id;

    String name;
    String abbreviation;

    @LastModifiedBy
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String lastModifiedBy;

    /**
     * Dynamic properties.
     */
    @CompositeProperty
            @JsonIgnore
    //@JsonSerialize(using = DynPropsSerializer.class, as=Map.class)
    Map<String, Object> dynamicProperties = new HashMap<>();

    /*
     * We need to provide mappers from certain SDN types to basic types
     * or Jackson will trip on them during serialization.
     */
    @JsonGetter("dynamicProperties")
    public Map<String,Object> serializeCustomProperties() {
        Map<String,Object> result = new HashMap<>(dynamicProperties.size());
        dynamicProperties.forEach((k,v) -> {
            if (v instanceof IntegerValue)
                result.put(k, ((IntegerValue) v).asInt());
            else if (v instanceof FloatValue)
                result.put(k, ((FloatValue) v).asDouble());
            else if (v instanceof ValueAdapter)
                result.put(k, ((ValueAdapter) v).asString());
            else
                result.put(k,v);
        });
        return result;
    }

    @JsonSetter("dynamicProperties")
    public void deserializeCustomProperties(Map<String, Object> props) {
        dynamicProperties.clear();
        dynamicProperties.putAll(props);
    }

    public static EntityVersion newInstance(String name, String abbreviation,
                                            @NonNull Map<String, Object> properties) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(properties);
        return new EntityVersion(null, name, abbreviation, null,
                map);
    }
}
