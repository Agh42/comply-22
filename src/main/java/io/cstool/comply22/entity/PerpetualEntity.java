package io.cstool.comply22.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.cstool.comply22.entity.relations.TimedRelation;
import io.cstool.comply22.entity.relations.VersionOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static io.cstool.comply22.entity.Change.ChangeType.INSERT;
import static io.cstool.comply22.entity.Change.ChangeType.UPDATE;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

@Node("Entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
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
    private Map<String, TimedRelation> dynamicRelationships = new HashMap<>();

    @Relationship(type = "VERSION_OF", direction = INCOMING)
    private Set<VersionOf> versionOf = new HashSet<>();

//    /**
//     * Pointers to the current version. With multiple timelines, there can be many current versions: one in each
//     * timeline.
//     */
//    @Relationship(type = "CURRENT")
//    @JsonIgnore
//    private Set<EntityVersion> currentVersion  = new HashSet<>();

//    @JsonProperty(access = READ_ONLY)
//    private Set<EntityVersionRef> getCurrentVersionRef() {
//        return currentVersion.stream()
//                .map(EntityVersionRef::of)
//                .collect(Collectors.toUnmodifiableSet());
//    }

    public static PerpetualEntity newInstance(String label) {
        var entity = new PerpetualEntity();
        entity.setCustomLabel(label);
        log.debug("Create new perpetual entity, label: {}", label);
        return entity;
    }

    public static PerpetualEntity newInstance() {
        return new PerpetualEntity();
    }

    /**
     * Creates the first version of this entity - when the entity is inserted into the repository.
     */
    public EntityVersion insert(String name, String abbreviation, Map<String, Object> properties,
                                Instant recorded) {
        var version = createVersion(name, abbreviation, properties);
        version.getChange().setType(INSERT);
        version.getChange().setRecorded(recorded);
        log.debug("Create first entity version, recorded: {}", recorded);
        return version;
    }

    /**
     * Creates another version of this entity when the entity is updated.
     */
    public EntityVersion update(EntityVersion versionDto) {
        var newVersion = createVersion(
                versionDto.getName(),
                versionDto.getAbbreviation(),
                versionDto.getDynamicProperties());
        newVersion.getChange().setType(UPDATE);
        return newVersion;
    }

    /**
     * Creates a version that represents the deleted state of this entity.
     */
    public EntityVersion delete(EntityVersion version) {
        // FIXME todo
        return null;
    }

    private EntityVersion createVersion(String name, String abbreviation, Map<String, Object> properties) {
        var version = EntityVersion.newInstance(name, abbreviation, properties);
        log.debug("Created new entity version, id: {}, change: {}", version.getId(), version.getChange());
        versionOf.add(VersionOf.relationshipTo(version));
//        currentVersion.add(version);
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

    public static String capitalize(String label) {
        return StringUtils.capitalize(label.toLowerCase());
    }

}
