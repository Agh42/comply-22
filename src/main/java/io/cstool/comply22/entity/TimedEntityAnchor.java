package io.cstool.comply22.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@Node("Entity")
@Data
@AllArgsConstructor
public class TimedEntityAnchor {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    @JsonProperty
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

    public static TimedEntityAnchor newInstance(Set<String> labels) {
        return new TimedEntityAnchor(null,
                labels,
                new HashMap<>(),
                new HashSet<>());
    }

    public TimedEntityVersion newVersion(String name, String abbreviation, Map<String, Object> properties) {
        var version= TimedEntityVersion.newInstance(name, abbreviation, properties);
        versionOf.add(VersionOf.relationShipTo(version));
        return version;
    }

    public Optional<TimedEntityVersion> getVersion(String id) {
        return versionOf.stream()
                .map(VersionOf::getEntityVersion)
                .filter(ev -> ev.getId().equals(id))
                .findFirst();
    }

    @JsonIgnore
    public Set<TimedEntityVersion> getVersions() {
        return versionOf.stream()
                .map(VersionOf::getEntityVersion)
                .collect(Collectors.toSet());
    }
}
