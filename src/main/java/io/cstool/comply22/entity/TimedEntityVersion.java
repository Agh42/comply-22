package io.cstool.comply22.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.Instant;
import java.util.*;

@Node("Version")
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class TimedEntityVersion {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    String name;
    String abbreviation;

    @LastModifiedBy
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String lastModifiedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer versionNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Instant from;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Instant until;

    @Version
    @JsonIgnore
    private int opLock;

    /**
     * Dynamic properties.
     */
    @JsonIgnore
    String propsJson;

    @JsonProperty
    public Map<String, Object> getProperties() throws JsonProcessingException {
        return objectMapper.readValue(propsJson, Map.class);
    }

    @JsonProperty
    public void setProperties(Map<String, Object> props) throws JsonProcessingException {
        propsJson = objectMapper.writeValueAsString(props);
    }


}
