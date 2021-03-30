package io.cstool.comply22.entity;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cstool.comply22.adapter.DynPropsSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.neo4j.core.schema.CompositeProperty;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

@Node("Version")
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EntityVersion {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final DynPropsSerializer dynPropsSerializer = new DynPropsSerializer();


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
    Map<String, Object> dynamicProperties;

    @JsonGetter("dynamicProperties")
    public Map<String,Object> serializeCustomProperties() {
        return dynPropsSerializer.serialize(dynamicProperties);
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
