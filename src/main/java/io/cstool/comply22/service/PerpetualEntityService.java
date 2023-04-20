package io.cstool.comply22.service;

import io.cstool.comply22.dto.request.CreateEntityDto;
import io.cstool.comply22.dto.response.EntityVersionDto;
import io.cstool.comply22.entity.EntityVersion;
import io.cstool.comply22.entity.PerpetualEntity;
import io.cstool.comply22.entity.PerpetualEntityRef;
import io.cstool.comply22.repository.ChangeRepository;
import io.cstool.comply22.repository.EntityVersionRepository;
import io.cstool.comply22.repository.PerpetualEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

import static io.cstool.comply22.entity.PerpetualEntity.capitalize;
import static io.cstool.comply22.entity.Reality.timeLineOrDefault;
import static java.lang.String.format;
import static java.util.Objects.requireNonNullElse;

@Service
@Slf4j
public class PerpetualEntityService {

//    private static final String QUERY = "MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
//            "WHERE id(a) = $id " +
//            //"AND 'Control' IN labels(a) " +
//            "WITH a,v,r " +
//            "ORDER BY v.from DESC " +
//            "LIMIT 1 " +
//            "RETURN a, collect(r), collect(v)";


    PerpetualEntityRepository entityRepository;

    EntityVersionRepository versionRepository;

    ChangeRepository changeRepository;

    Neo4jTemplate template;

    public PerpetualEntityService(PerpetualEntityRepository entityRepository, EntityVersionRepository versionRepository,
                                  ChangeRepository changeRepository, Neo4jTemplate template) {
        this.entityRepository = entityRepository;
        this.versionRepository = versionRepository;
        this.changeRepository = changeRepository;
        this.template = template;
    }

    @Transactional
    public EntityVersionDto createEntity(@NotNull String label, @Nullable String timeline,
                                         Instant timestamp, CreateEntityDto dto) {
        label = capitalize(label);
        timeline = timeLineOrDefault(timeline);
        timestamp = requireNonNullElse(timestamp, Instant.now());

        // insert entity:
        var anchor = PerpetualEntity.newInstance(label);
        var version = anchor.insert(
                dto.getVersion().getName(),
                dto.getVersion().getAbbreviation(),
                dto.getVersion().getDynamicProperties(),
                timestamp);
        anchor = entityRepository.save(anchor);
        log.debug("Saved new entity: {}", anchor);

        // insert version:
        version = versionRepository.save(version);
        log.debug("Saved new first version: {}", version);

        // make version current:
        log.debug("Creating current-pointer to version {} from entity {}.", version.getId(), anchor.getId());
        versionRepository.mergeCurrentVersionWithNewEntity(anchor.getId(), version.getId());

        // update reality tree:
        var change = changeRepository.save(version.getChange());
        log.debug("Saved change, id: {}, recorded: {}.", change.getId(), change.getRecorded());
        version.setChange(change);
        log.debug("Making change {} the tip of timeline {}", change.getId(), timeline);
        changeRepository.mergeWithTimeline(timeline, change.getId());

        anchor = entityRepository.findById(anchor.getId()).orElseThrow();
        version = anchor.getVersion(version.getId()).orElseThrow();
        log.debug("Saved change: {}", version.getChange());
        return new EntityVersionDto(new PerpetualEntityRef(anchor.getId()), version);
    }

    /**
     * Find the latest version of an entity.
     */
    public EntityVersion find(String label, String timeline, Long perpetualId) {
        label = capitalize(label);
        Map<String,Object> params = Map.of("customlabel", label,
                "id", perpetualId);
        // FIXME custom labels not filled:
        //entity.setCustomLabels(Set.copyOf(entityRepository.findLabelsForNode(perpetualId)));
        return versionRepository.findLatestVersion(label, timeline,
                perpetualId).orElseThrow();
    }

    /**
     * Find specific version by its ID.
     */
    public EntityVersion find(String label, Long perpetualId, Long versionId) {
        // TODO add custom query to repository to speed up execution

        label = capitalize(label);
        var entity = entityRepository.findById(perpetualId).orElseThrow();
        var version = versionRepository.findById(versionId).orElseThrow();
        // Check entity relationship:
        if (entity.getVersion(versionId).isEmpty()) {
            throw new IllegalArgumentException(format(
                    "Version ID %s not found for entity ID %s",
                    versionId, perpetualId));
        }
        // Check label is correct:
        if (!entity.getCustomLabels().contains(label)) {
            throw new IllegalArgumentException(
                    format("Wrong label %s for entity ID %s",
                            label, perpetualId));
        }
        return version;
    }

    /**
     * Find version at specific timestamp
     */
    public EntityVersion find(String label, String timeline, Long perpetualId, Instant timestamp) {
        label = capitalize(label); // FIXME fix norootnodemappingexpection when running test "get current version"
        return versionRepository.findCurrentVersionAt(label, perpetualId, timeline, timestamp)
                .orElse(versionRepository.findPreviousVersionAt(label, perpetualId, timeline, timestamp)
                        .orElseThrow(() -> new VersionNotFoundException(
                                "No version was found for entity %s in timeline %s " +
                                        "at specified timestamp %s", perpetualId, timeline, timestamp))
                );
    }
    // TODO xxx run test to see if this works for timestamp at tip and between updated changes

    public Slice<PerpetualEntity> findAllCurrent(String label, Pageable pageable) {
        label = capitalize(label);
        return entityRepository.findAllCurrent(label, pageable);
    }

    public void deleteAll(String label) {
        // FIXME insert deleted version for all entities in $timeline
        label = capitalize(label);
        entityRepository.deleteAllByLabel(label);
    }

    /**
     * Updates the most current version of the entity in this timeline.
     *
     * @param timelineParam the timeline or {@code null} for default timeline
     * @param labelParam the label of the entity - its type
     * @param entity a reference to the requested entity
     * @param timestampParam the time at which to update the entity or {@code null} for now
     * @param dtoVersion a new version with values that should be saved
     */
    @Transactional
    public void updateEntity(@Nullable String timelineParam,
                             String labelParam,
                             PerpetualEntityRef entity,
                             @Nullable Instant timestampParam,
                             EntityVersion dtoVersion) {
        var timeline = timeLineOrDefault(timelineParam);
        var timestamp = requireNonNullElse(timestampParam, Instant.now());
        var label = capitalize(labelParam);

        var previousLatestVersion = versionRepository
                .findLatestVersion(label, timeline, entity.getId()).orElseThrow(
                        () -> new IllegalArgumentException(String.format("Entity %s does not have a version that " +
                                "can be updated in timeline %s.", entity.getId(), timeline))
                );

        if ( timestamp.isBefore(previousLatestVersion.getChange().getRecorded()) )
            throw new IllegalArgumentException(
                    String.format("New version with timestamp %s cannot be saved before previous version " +
                            "with timestamp %s in timeline %s for entity %s. Use a later timestamp or create a new timeline.",
                            timestamp, previousLatestVersion.getFrom(), timeline, entity.getId()));

        // Create new version of the entity. This also creates a new change:
        var anchor = entityRepository.findById(entity.getId()).orElseThrow();

        if (!entityRepository.findLabelsForNode(entity.getId()).contains(label))
            throw new IllegalArgumentException(
                    String.format("Node ID %s does not match node label %s",
                            entity.getId(), label)
            );

        var updatedVersion = anchor.update(dtoVersion);

        updatedVersion = versionRepository.save(updatedVersion);
        log.debug("Saved version: {}", updatedVersion);

        anchor = entityRepository.save(anchor);
        log.debug("Saved entity: {}", anchor);

        // Adjust timestamps and move the "current" pointer of the entity forward in this timeline:
        log.debug("Merging version {} in timeline {} with timestamp {}", updatedVersion.getId(), timeline,
                timestamp);
        versionRepository.mergeVersionWithEntity(timeline,
                entity.getId(),
                updatedVersion.getId(),
                timestamp);
        log.debug("Made version {} current in timeline {} with timestamp {}", updatedVersion.getId(), timeline,
                timestamp);

        // make the new version's change the tip of this timeline:
        log.debug("Moving tip of timeline {} forward to change {}",
                timeline, updatedVersion.getChange().getId());
        changeRepository.mergeWithTimeline(timeline,
                updatedVersion.getChange().getId());
        log.debug("Moved tip of timeline {} forward to change {}",
                timeline, updatedVersion.getChange().getId());
        var updatedChange = changeRepository.findById(updatedVersion.getChange().getId()).orElseThrow();

        // link previous change on same entity to new change:
        log.debug("Linking related changes  {} -> {} in timeline {}",
                previousLatestVersion.getChange(), updatedVersion.getChange().getId(), timeline);
        previousLatestVersion = versionRepository.findById(previousLatestVersion.getId()).orElseThrow();
        previousLatestVersion.getChange().getNextRelatedChanges().add(updatedChange);
        changeRepository.save(previousLatestVersion.getChange());
        log.debug("Linked related changes  {} -> {} in timeline {}",
                previousLatestVersion.getChange(), updatedVersion.getChange().getId(), timeline);

//        updatedVersion = versionRepository.save(updatedVersion);
//        log.debug("Saved updatedVersion: {}", updatedVersion);
    }
}