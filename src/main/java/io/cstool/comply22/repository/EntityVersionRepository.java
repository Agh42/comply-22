package io.cstool.comply22.repository;

import io.cstool.comply22.entity.EntityVersion;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.time.Instant;
import java.util.Optional;

public interface EntityVersionRepository extends Neo4jRepository<EntityVersion, Long>,
        NonDomainResults {



    /**
     * Find the last known version of the entity in the given timeline.
     * <p>
     * If the entity has been deleted this will return the last known version. This version will carry the
     * "deleted" flag.
     */
    @Query("MATCH p = (e:Entity)-[c:CURRENT]->(v:Version)-[:RECORDED_ON|NEXT|TIP_OF*]->(r:Reality{name:$timeline}) " +
            "WHERE id(e) = $perpetualId " +
            "AND $label IN labels(e) " +
            "WITH e,v " +
            "MATCH (v)-[ro:RECORDED_ON]->(c:Change) " +
            "RETURN v, collect(ro), collect(c)"
    )
    Optional<EntityVersion> findLatestVersion(String label, String timeline,
                                                    Long perpetualId);


    /**
     * Finds a version of an entity that was valid at the given point in time in the given timeline.
     * <p>
     * Uses the change and related-change of the version to determine if it was valid in the requested timeline. This
     * query only finds versions that have been updated, meaning that they have a change before and past the specified
     * timestamp:
     * <pre>
     * (from:Change)-...->(until:Change)
     *                ^
     *                |
     *             Timestamp
     * </pre>
     *
     */
    @Query("""
            MATCH (a:Entity)<-[r:VERSION_OF]-(v:Version)-[:RECORDED_ON]->(cf:Change)-[:NEXT|TIP_OF*]->(re:Reality{name:$timeline})
            WHERE id(a) = $id
            AND $label IN labels(a)
            AND ( cf.recorded <= $time )
            WITH a,v,r,cf
            MATCH p=(cf)-[:NEXT_RELATED]->(cu:Change)-[:NEXT|TIP_OF*]->(re:Reality{name:$timeline})
            WHERE cu.recorded >= $time
            RETURN a, collect(r), collect(v)
            """)
    Optional<EntityVersion> findPreviousVersionAt(String label, Long id, String timeline, Instant time);

    /**
     * Finds a version of an entity that is currently valid at the given point in time in the given timeline.
     * <p>
     * Uses the change and related-change of the version to determine if it was valid in the requested timeline.
     * If a change but no no related-change is found, this means that the version is the current one in its timeline,
     * and it will be returned.
     */
    @Query("""
            MATCH (a:Entity)<-[r:VERSION_OF]-(v:Version)-[:RECORDED_ON]->(cf:Change)-[:NEXT|TIP_OF*]->(re:Reality{name:$timeline})
            WHERE id(a) = $id
            AND $label IN labels(a)
            AND ( cf.recorded <= $time )
            AND NOT exists( (cf)-[:NEXT_RELATED]->()-[:NEXT|TIP_OF*]->(re:Reality{name:$timeline}) )
            RETURN a, collect(r), collect(v)
            """)
    Optional<EntityVersion> findCurrentVersionAt(String label, Long id, String timeline, Instant time);

}