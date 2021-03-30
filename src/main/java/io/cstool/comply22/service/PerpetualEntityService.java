package io.cstool.comply22.service;

import io.cstool.comply22.entity.EntityDto;
import io.cstool.comply22.entity.PerpetualEntity;
import io.cstool.comply22.repository.PerpetualEntityRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class PerpetualEntityService {

    private static final String QUERY = "MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE id(a) = $id " +
            //"AND 'Control' IN labels(a) " +
            "AND r.reality = 0 " +
            "WITH a,v,r " +
            "ORDER BY v.from DESC " +
            "LIMIT 1 " +
            "RETURN a, collect(r), collect(v)";


    PerpetualEntityRepository entityRepository;

    Neo4jTemplate template;

    public PerpetualEntityService(PerpetualEntityRepository entityRepository, Neo4jTemplate template) {
        this.entityRepository = entityRepository;
        this.template = template;
    }

    public EntityDto createEntity(String label, EntityDto dto) {
        // if dto has labels, replace them with path variable:
        var anchor = PerpetualEntity.newInstance(label);

        var version = anchor.newVersion(
                dto.getVersion().getName(),
                dto.getVersion().getAbbreviation(),
                dto.getVersion().getDynamicProperties());
        anchor = entityRepository.save(anchor);
        return new EntityDto(
                anchor,
                anchor.getVersionOf(),
                anchor.getVersions().stream().findFirst().orElseThrow());
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