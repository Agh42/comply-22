package io.cstool.comply22.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.Instant;
import java.util.*;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@Node("Entity")
@Data
@AllArgsConstructor
public class TimedEntityAnchor {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @DynamicLabels
    private Set<String> labels;

    // dynamic relationships:
    @Relationship
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<String, TimedRelation> dynamicRelationships = new HashMap<>();

    @Relationship(type = "VERSION_OF", direction = INCOMING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<VersionOf> versionOf = new HashSet<>();

    @JsonIgnore

    public static TimedEntityAnchor newInstance(Set<String> labels) {
        return new TimedEntityAnchor(null,
                labels,
                new HashMap<>(),
                new HashSet<>());
    }

    public TimedEntityVersion newVersion(String name, String abbreviation, Map properties) {
        var propString = new ObjectMapper().valueToTree(properties).toString();
        var version= new TimedEntityVersion(null,
                name,
                abbreviation,
                null,
                0,
                Instant.now(),
                null,
                propString);
        versionOf.add(VersionOf.relationShipTo(version));
        return version;
    }

    public Optional<TimedEntityVersion> getVersion(Integer number) {
        return versionOf.stream()
                .map(VersionOf::getEntityVersion)
                .filter(ev -> ev.getVersionNumber().equals(number))
                .findFirst();
    }
}
