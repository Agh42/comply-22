package io.cstool.comply22.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@Node("Entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerpetualEntity {

    private static final String LABEL = "Entity";
    @Id
    @GeneratedValue
    @JsonProperty
    private Long id;

    @DynamicLabels
    public Set<String> customLabels = new HashSet<>();

    public void setCustomLabel(String label) {
        customLabels.clear();
        customLabels.add(label);
    }

    public void setCustomLabels(Set<String> customLabels) {
        this.customLabels = customLabels.stream()
                .filter(s -> !s.equals(PerpetualEntity.LABEL))
                .collect(Collectors.toSet());
    }

    // dynamic relationships:
    @Relationship
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<String, TimedRelation> dynamicRelationships = new HashMap<>();

    @Relationship(type = "VERSION_OF", direction = INCOMING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<VersionOf> versionOf = new HashSet<>();

    /**
     * Pointers to the current version. With multiple timelines, there can be
     * many current versions: one in each timeline.
     */
    @Relationship(type = "CURRENT")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<EntityVersion> currentVersion = new HashSet<>();

    public static PerpetualEntity newInstance(String label) {
        var entity = new PerpetualEntity();
        entity.setCustomLabel(label);
        return entity;
    }

    public static PerpetualEntity newInstance() {
        return new PerpetualEntity();
    }

    public EntityVersion newVersion(String name, String abbreviation, Map<String, Object> properties) {
        var version= EntityVersion.newInstance(name, abbreviation, properties);
        versionOf.add(VersionOf.relationShipTo(version));
        currentVersion.add(version);
        return version;
    }

    public Optional<EntityVersion> getVersion(Long id) {
        return versionOf.stream()
                .map(VersionOf::getEntityVersion)
                .filter(ev -> ev.getId().equals(id))
                .findFirst();
    }

    @JsonIgnore
    public Set<EntityVersion> getVersions() {
        return versionOf.stream()
                .map(VersionOf::getEntityVersion)
                .collect(Collectors.toSet());
    }
}
