package io.cstool.comply22.repository;

import io.cstool.comply22.entity.PerpetualEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Transactional(readOnly = true)
public interface PerpetualEntityRepository extends Neo4jRepository<PerpetualEntity, Long>,
    NonDomainResults {


    /**
     * Find the last known version of the entity. If the entity has been deleted this
     * will return the last known version. This version will have its validity period set in the past
     * and additionally carry the "deleted" flag.
     *
     * @param id
     * @return
     */
    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE id(a) = $id " +
            "AND $label IN labels(a) " +
            "WITH a,v,r " +
            "ORDER BY v.from DESC " +
            "LIMIT 1 " +
            "RETURN a, collect(r), collect(v) "
    )
    public Optional<PerpetualEntity> findLatestVersion(@Param("label") String label, Long id);

    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE id(a) = $id " +
            "AND $label IN labels(a)  " +
            "AND (v.from < $time) AND ($time < v.until OR NOT EXISTS(v.until)) " +
            "WITH a,v,r " +
            "RETURN a, collect(r), collect(v)"
    )
    public Optional<PerpetualEntity> findVersionAt(String label, Long id, Instant time);

    /**
     * Find all entities for the present time. Deleted entities will not be included in
     * the result.
     *
     * @param pageable
     * @return
     */
    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE $label IN labels(a) " +
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