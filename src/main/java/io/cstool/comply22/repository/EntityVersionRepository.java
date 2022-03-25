package io.cstool.comply22.repository;

import io.cstool.comply22.entity.EntityVersion;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

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


}