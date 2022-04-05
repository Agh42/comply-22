package io.cstool.comply22.repository;

import io.cstool.comply22.entity.PerpetualEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface PerpetualEntityRepository extends Neo4jRepository<PerpetualEntity, Long>,
    NonDomainResults {

    /**
     * Find all entities of the given type for the present time. Deleted entities will not be included in
     * the result.
     *
     */
    @Query("MATCH (a:Entity) -[r:CURRENT]-> (v:Version) " +
            "WHERE $label IN labels(a) " +
            "AND v.deleted = FALSE " +
            "RETURN a, collect(r), collect(v) " +
            "ORDER BY a.id ASC " +
            "SKIP $skip LIMIT $limit "
    )
    Slice<PerpetualEntity> findAllCurrent(@Param("label") String label, Pageable pageable);



// all versions for one id
//    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
//            "WHERE a.id = $id " +
//            "RETURN a,collect(v) " +
//            "ORDER BY v.from ASC " +
//            "SKIP $skip LIMIT $limit"
//    )
//    Slice<EntityDto> findHistory(String id, Pageable pageable);
}