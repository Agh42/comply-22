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
     * Uses the change and related-change of the version to determine if it was valid in the requested timeline.
     * If a change but no no related-change is found, this means that the version is the current one in its timeline,
     * and it will be returned.
     */
    @Query("MATCH (a:Entity)<-[r:VERSION_OF]-(v:Version) " +
            "WHERE id(a) = $id " +
            "AND $label IN labels(a) " +
            "WITH a,v,r " +
            "MATCH (v)-[:RECORDED_ON]->(cf:Change)-[:NEXT|TIP_OF*]->(re:Reality{name:$timeline}) " +
            "WHERE ( cf.recorded < $time ) " +
            "WITH a,v,r,cf " +
            "MATCH (cf)-[:NEXT_RELATED]->(cu:Change)-[:NEXT|TIP_OF*]->(re:Reality{name:$timeline}) " +
            "WHERE ( cf.recorded < $time) AND ($time < cu.recorded OR cu.recorded IS NOT NULL ) " +
            "WITH a,v,r " +
            "RETURN a, collect(r), collect(v)"
    )
    // MATCH (a:Entity)<-[r:VERSION_OF]-(v:Version)
    //WHERE id(a) = 10504
    //AND 'Mytype' IN labels(a)
    //WITH a,v,r
    //MATCH (v)-[:RECORDED_ON]->(cf:Change)-[:NEXT|TIP_OF*]->(re:Reality{name:'C-137'})
    //WHERE ( cf.recorded < datetime('2022-04-02T15:21:04.079865980Z') )
    //WITH a,v,r,cf
    //OPTIONAL MATCH p=(cf)-[:NEXT_RELATED]->(cu:Change)-[:NEXT|TIP_OF*]->(re:Reality{name:'C-137'})
    //WHERE ( cf.recorded < datetime('2022-04-02T15:21:04.079865980Z')) AND ( datetime('2022-04-02T15:21:04.079865980Z') < cu.recorded OR cu.recorded IS NULL ) xxx add an update to 10504, this select should return both changes as is. fix to return only newest xxx
    //WITH a,v,r
    //RETURN a, collect(r), collect(v)
    Optional<EntityVersion> findVersionAt(String label, Long id, String timeline, Instant time);

}