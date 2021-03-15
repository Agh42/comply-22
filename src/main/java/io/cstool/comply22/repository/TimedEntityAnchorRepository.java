package io.cstool.comply22.repository;

import io.cstool.comply22.entity.EntityDto;
import io.cstool.comply22.entity.TimedEntityAnchor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface TimedEntityAnchorRepository extends PagingAndSortingRepository<TimedEntityAnchor, String> {

    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE v.id = $versionId " +
            "RETURN a,v")
    public Optional<EntityDto> findSpecificVersion(String versionId);

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
            "RETURN a,v " +
            "ORDER BY v.from DESC " +
            "LIMIT 1 "
    )
    public Optional<EntityDto> findLatestVersion(String id);

    @Query("MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE a.id = $id " +
            "AND (v.from < $time) AND ($time < v.until OR NOT EXISTS(v.until)) " +
            "RETURN a,v")
    public Optional<EntityDto> findVersionAt(String id, Instant time);

    /**
     * Find all entites for the present time. Deleted entities will not be included in
     * the result.
     *
     * @param pageable
     * @return
     */
    @Query(value="MATCH (a:Entity) <-[r:VERSION_OF]- (v:Version) " +
            "WHERE v.until IS null " +
            "RETURN a,collect(v) " +
            "ORDER BY v.name ASC " +
            "SKIP $skip LIMIT $limit "
    )
    Slice<EntityDto> findAllCurrent(Pageable pageable);
}