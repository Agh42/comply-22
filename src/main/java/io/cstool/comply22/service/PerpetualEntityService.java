package io.cstool.comply22.service;

import io.cstool.comply22.dto.CreateEntityDto;
import io.cstool.comply22.entity.PerpetualEntity;
import io.cstool.comply22.entity.Reality;
import io.cstool.comply22.repository.ChangeRepository;
import io.cstool.comply22.repository.EntityVersionRepository;
import io.cstool.comply22.repository.PerpetualEntityRepository;
import io.cstool.comply22.repository.RealityRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNullElse;

@Service
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

    RealityRepository realityRepository;

    Neo4jTemplate template;

    public PerpetualEntityService(PerpetualEntityRepository entityRepository, Neo4jTemplate template) {
        this.entityRepository = entityRepository;
        this.template = template;
    }

//    @PostConstruct
//    void init() {
//        var mainstream = realityRepository.findByName(Reality.MAINSTREAM).stream().findFirst();
//        if (mainstream.isEmpty()) {
//            realityRepository.save(new Reality(Reality.MAINSTREAM));
//        }
//    }

    @Transactional
    public CreateEntityDto createEntity(@NotNull String label, @Nullable String timeline, CreateEntityDto dto) {
        timeline = requireNonNullElse(timeline, Reality.MAINSTREAM);
        timeline = timeline.isBlank() ? Reality.MAINSTREAM : timeline;

        // insert entity:
        var anchor = PerpetualEntity.newInstance(label);
        anchor = entityRepository.save(anchor);

        // insert version:
        var version = anchor.newVersion(
                dto.getVersion().getName(),
                dto.getVersion().getAbbreviation(),
                dto.getVersion().getDynamicProperties());
        version = versionRepository.save(version);
        version = versionRepository.mergeVersionWithEntity(timeline, anchor.getId(), version.getId());

        // update reality tree:
        changeRepository.mergeWithTimeline(timeline, version.getChange().getId());

        return new CreateEntityDto(version);
    }

    /**
     * Find the latest version of an entity.
     * @return
     */
    public PerpetualEntity find(String label, Long id) {
        Map<String,Object> params = Map.of("customlabel", label,
                "id", id);
        // FIXME custom labels not filled:
        var entity = entityRepository.findLatestVersion(label, id).orElseThrow();
        entity.setCustomLabels(Set.copyOf(entityRepository.findLabelsForNode(id)));
        return entity;
    }

    public Optional<PerpetualEntity> find(String label, Long id, Instant timestamp) {
        return entityRepository.findVersionAt(label, id, timestamp);
    }

    public Slice<PerpetualEntity> findAllCurrent(String label, Pageable pageable) {
        return entityRepository.findAllCurrent(label, pageable);
    }

    public void deleteAll(String label) {
        entityRepository.deleteAllByLabel(label);
    }
}