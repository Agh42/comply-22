package io.cstool.comply22.repository;

import io.cstool.comply22.entity.EntityDto;
import io.cstool.comply22.entity.PerpetualEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PerpetualEntityRepository extends PagingAndSortingRepository<PerpetualEntity, Long>,
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
            //"AND $label IN labels(a)  " +
            "AND r.reality = 0  " +
            "WITH a,v,r " +
            "ORDER BY v.from DESC " +
            "LIMIT 1 " +
            "RETURN a, collect(r), collect(v)"
    )
    public Optional<PerpetualEntity> findLatestVersion(String label, Long id);

    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE a.id = $id " +
            "AND $label IN labels(a)  " +
            "AND (v.from < $time) AND ($time < v.until OR NOT EXISTS(v.until)) " +
            "AND r.reality = 0  " +
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
    @Query(value="MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE v.until IS null " +
            "AND $label IN labels(a)  " +
            "RETURN a,collect(v) " +
            "ORDER BY v.from ASC " +
            "SKIP $skip LIMIT $limit"
    )
    Slice<EntityDto> findAllCurrent(String label, Pageable pageable);

// all versions for one id
//    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
//            "WHERE a.id = $id " +
//            "RETURN a,collect(v) " +
//            "ORDER BY v.from ASC " +
//            "SKIP $skip LIMIT $limit"
//    )
//    Slice<EntityDto> findHistory(String id, Pageable pageable);
}