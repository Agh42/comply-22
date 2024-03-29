package io.cstool.comply22.entity;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.cstool.comply22.adapter.DynPropsSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Node("Version")
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EntityVersion {

    private static final DynPropsSerializer dynPropsSerializer = new DynPropsSerializer();

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(max = 255)
    @NotBlank
    private String name;

    @Size(max = 255)
    private String abbreviation;

    @LastModifiedBy
    private String lastModifiedBy;

    /**
     * Dynamic properties.
     */
    @CompositeProperty
    @JsonIgnore
    //@JsonSerialize(using = DynPropsSerializer.class, as=Map.class)
    private Map<String, Object> dynamicProperties;

    @Relationship(type = "RECORDED_ON")
    @JsonIgnore
    private Change change;

    @JsonGetter("change")
    private ChangeRef getChangeRef() {
        return ChangeRef.of(change);
    }

    /**
     * Specifies from which time this version was valid in its timeline.
     */
    private Instant from;

    /**
     * Specifies until which time this version was valid in its timeline.
     * <p>
     * Will be null for versions that are the current one in their timeline.
     */
    private Instant until;

    private boolean deleted;

    @JsonGetter("dynamicProperties")
    public Map<String, Object> serializeCustomProperties() {
        return dynPropsSerializer.serialize(dynamicProperties);
    }

    @JsonSetter("dynamicProperties")
    public void setCustomProperties(Map<String,Object> props) {
        if (dynamicProperties == null)
            dynamicProperties = new HashMap<>();
        else
            dynamicProperties.clear();
        dynamicProperties.putAll(props);
    }

    public static EntityVersion newInstance(String name, String abbreviation,
                                            @Nullable Map<String, Object> properties) {
        Map<String, Object> dynamicProperties;
        if (properties == null)
            dynamicProperties = new HashMap<>();
        else
            dynamicProperties = new HashMap<>(properties);
        return new EntityVersion(null, name, abbreviation, null,
                dynamicProperties, new Change(Instant.now()),
                Instant.now(), null, false);
    }
}
