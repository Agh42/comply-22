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
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Node("Version")
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EntityVersion {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final DynPropsSerializer dynPropsSerializer = new DynPropsSerializer();

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(max = 255)
    @NotBlank
    String name;

    String abbreviation;

    @LastModifiedBy
    @JsonProperty(access = READ_ONLY)
    String lastModifiedBy;

    /**
     * Dynamic properties.
     */
    @CompositeProperty
    @JsonIgnore
    //@JsonSerialize(using = DynPropsSerializer.class, as=Map.class)
    Map<String, Object> dynamicProperties;

    @Relationship(type = "RECORDED_ON")
    @JsonIgnore
    private Change change;

    @JsonProperty(access = READ_ONLY)
    private ChangeRef getChangeRef() {
        return ChangeRef.of(change);
    }


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
                map, new Change());
    }
}
