package io.cstool.comply22.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.*;

@Node("Version")
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class TimedEntityVersion {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    String name;
    String abbreviation;

    @LastModifiedBy
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String lastModifiedBy;

    /**
     * Dynamic properties.
     */
    @CompositeProperty
    Map<String, Object> properties = new HashMap<>();

    public static TimedEntityVersion newInstance(String name, String abbreviation,
                                                 @NonNull Map<String, Object> properties) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(properties);
        return new TimedEntityVersion(null, name, abbreviation, null,
                map);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }
}
