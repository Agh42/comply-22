package io.cstool.comply22.repository;

import io.cstool.comply22.entity.EntityDto;
import io.cstool.comply22.entity.TimedEntityAnchor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.Instant;
import java.util.Optional;

public interface TimedEntityAnchorRepository extends PagingAndSortingRepository<TimedEntityAnchor, String> {

    /**
     * Find the last known version of the entity. If the entity has been deleted this
     * will return the last known version. This version will have its validity period set in the past
     * and additionally carry the "deleted" flag.
     *
     * @param id
     * @return
     */
    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE a.id = $id " +
            "AND r.reality = 0  " +
            "WITH a,v,r " +
            "ORDER BY v.from DESC " +
            "LIMIT 1 " +
            "RETURN a, collect(r), collect(v)"
    )
    public Optional<TimedEntityAnchor> findLatestVersion(String id);

    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE a.id = $id " +
            "AND (v.from < $time) AND ($time < v.until OR NOT EXISTS(v.until)) " +
            "RETURN a,v")
    public Optional<EntityDto> findVersionAt(String id, Instant time);

    /**
     * Find all entities for the present time. Deleted entities will not be included in
     * the result.
     *
     * @param pageable
     * @return
     */
    @Query(value="MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE v.until IS null " +
            "RETURN a,collect(v) " +
            "ORDER BY v.from ASC " +
            "SKIP $skip LIMIT $limit"
    )
    Slice<EntityDto> findAllCurrent(Pageable pageable);

    // all versions for one id
//    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
//            "WHERE a.id = $id " +
//            "RETURN a,collect(v) " +
//            "ORDER BY v.from ASC " +
//            "SKIP $skip LIMIT $limit"
//    )
//    Slice<EntityDto> findHistory(String id, Pageable pageable);
}