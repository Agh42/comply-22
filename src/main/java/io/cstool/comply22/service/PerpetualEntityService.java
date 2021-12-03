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
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import static io.cstool.comply22.entity.Reality.timeLineOrDefault;
import static java.lang.String.format;

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
                                         CreateEntityDto dto) {
        label = capitalize(label);
        timeline = timeLineOrDefault(timeline);

        // insert entity:
        var anchor = PerpetualEntity.newInstance(label);
        var version = anchor.insert(
                dto.getVersion().getName(),
                dto.getVersion().getAbbreviation(),
                dto.getVersion().getDynamicProperties());
        anchor = entityRepository.save(anchor);
        log.debug("Saved entity: {}", anchor);

        // insert version:
        version = versionRepository.save(version);
        log.debug("Saved version: {}", version);

        // update reality tree:
        var changeId = version.getChange().getId();
        changeRepository.mergeWithTimeline(timeline, changeId);

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
        var entity = entityRepository.findById(perpetualId).orElseThrow();
        var version = versionRepository.findById(versionId).orElseThrow();
        // Check entity relationship:
        if (entity.getVersion(versionId).isEmpty()) {
            throw new IllegalArgumentException(format(
                    "Version ID {} not found for entity ID {}",
                    versionId, perpetualId));
        }
        // Check label is correct:
        if (!entity.getCustomLabels().contains(label)) {
            throw new IllegalArgumentException(format("Wrong label {} for entity ID {}", label, perpetualId));
        }
        return version;
    }

    /**
     * Find version at specific timestamp
     */
    public EntityVersion find(String label, String timeline, Long perpetualId, Instant timestamp) {
        label = capitalize(label);
        return null;
        //return entityRepository.findVersionAt(label, id, timestamp);
    }

    public Slice<PerpetualEntity> findAllCurrent(String label, Pageable pageable) {
        label = capitalize(label);
        return entityRepository.findAllCurrent(label, pageable);
    }

    public void deleteAll(String label) {
        label = capitalize(label);
        entityRepository.deleteAllByLabel(label);
    }

    private String capitalize(String label) {
        return StringUtils.capitalize(label.toLowerCase());
    }

    @Transactional
    public void updateEntity(String timeline, PerpetualEntityRef entity, Instant timestamp, EntityVersion dtoVersion) {
        timeline = timeLineOrDefault(timeline);
        timestamp = Objects.requireNonNullElse(timestamp, Instant.now());

        // FIXME check if timestamp is past previous version's validFom

        // create new version of the entity (with its change):
        var anchor = entityRepository.findById(entity.getId()).orElseThrow();
        var updatedVersion = anchor.update(dtoVersion);
        anchor = entityRepository.save(anchor);
        log.debug("Saved entity: {}", anchor);

        // adjust timestamps and move the "current version" pointer of the entity forward in this timeline:
        versionRepository.mergeVersionWithEntity(timeline,
                entity.getId(),
                updatedVersion.getId(),
                timestamp);

        // make the new version's change the tip of this timeline:
        // TODO xxx
//        var changeId = version.getChange().getId();
//        changeRepository.mergeWithTimeline(timeline, changeId);
//
//        anchor = entityRepository.findById(anchor.getId()).orElseThrow();
//        version = anchor.getVersion(version.getId()).orElseThrow();
//        log.debug("Saved change: {}", version.getChange());


        updatedVersion = versionRepository.save(updatedVersion);
        log.debug("Saved updatedVersion: {}", updatedVersion);


    }
}