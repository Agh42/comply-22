package io.cstool.comply22.service;

import io.cstool.comply22.dto.request.CreateEntityDto;
import io.cstool.comply22.dto.response.CreatedEntityDto;
import io.cstool.comply22.entity.PerpetualEntity;
import io.cstool.comply22.entity.Reality;
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
import java.util.Optional;
import java.util.Set;

import static io.cstool.comply22.entity.Change.ChangeType.INSERT;
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
    public CreatedEntityDto createEntity(@NotNull String label, @Nullable String timeline, CreateEntityDto dto) {
        label = capitalize(label);
        timeline = requireNonNullElse(timeline, Reality.MAINSTREAM);
        timeline = timeline.isBlank() ? Reality.MAINSTREAM : timeline;

        // insert entity:
        var anchor = PerpetualEntity.newInstance(label);
        var version = anchor.newVersion(
                dto.getVersion().getName(),
                dto.getVersion().getAbbreviation(),
                dto.getVersion().getDynamicProperties());
        version.getChange().setType(INSERT);
        anchor = entityRepository.save(anchor);
        log.debug("Saved entity: {}", anchor);

        // insert version:
        version = versionRepository.save(version);
        log.debug("Saved version: {}", version);
        //version = versionRepository.mergeVersionWithEntity(timeline, anchor.getId(), version.getId()); // merge second and later versions

        // update reality tree:
        var changeId = version.getChange().getId();
        changeRepository.mergeWithTimeline(timeline, changeId);

        anchor = entityRepository.findById(anchor.getId()).orElseThrow();
        version = anchor.getVersion(version.getId()).orElseThrow();
        log.debug("Saved change: {}", version.getChange());
        return new CreatedEntityDto(version);
    }

    /**
     * Find the latest version of an entity.
     */
    public PerpetualEntity find(String label, Long id) {
        label = capitalize(label);
        Map<String,Object> params = Map.of("customlabel", label,
                "id", id);
        // FIXME custom labels not filled:
        var entity = entityRepository.findLatestVersion(label, id).orElseThrow();
        entity.setCustomLabels(Set.copyOf(entityRepository.findLabelsForNode(id)));
        return entity;
    }

    public Optional<PerpetualEntity> find(String label, Long id, Instant timestamp) {
        label = capitalize(label);
        return entityRepository.findVersionAt(label, id, timestamp);
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
}