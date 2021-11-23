package io.cstool.comply22.repository;

import io.cstool.comply22.entity.EntityVersion;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

public interface EntityVersionRepository extends Neo4jRepository<EntityVersion, Long>,
        NonDomainResults {

    /**
     * <ul>
     * <li>Find most recent version of entity in the given timeline.
     * <li>Set the "validUntil" timestamp of its pointer to 'now'.
     * <li>Set "validFrom" on the pointer of the new version to the same 'now'.
     * <li>Then move the current-pointer of the entity to the new version.
     * </ul>
     */
    @Transactional(propagation = REQUIRED)
    @Query("MATCH p = (e:Entity)-[c:CURRENT]->(old:Version)-[:RECORDED_ON|NEXT|TIP_OF*]->(r:Reality{name:$reality}) " +
            "WHERE id(e) = $perpetualEntityId " +
            "WITH e,c,old " +
            "MATCH (nv:Version) WHERE id(nv) = $newVersionId " +
            "DELETE c " +
            "SET old.until = datetime.transaction() " +
            "SET nv.from = datetime.transaction() " +
            "MERGE (e)-[:CURRENT]->(nv) ")
    // TODO move to nondomain repo
    EntityVersion mergeVersionWithEntity(String reality, Long perpetualEntityId, Long newVersionId);

    /**
     * Find the last known version of the entity in the given timeline.
     * <p>
     * If the entity has been deleted this will return the last known version. This version will have its validity
     * period set in the past and additionally carry the "deleted" flag.
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