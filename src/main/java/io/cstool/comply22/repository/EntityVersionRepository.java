package io.cstool.comply22.repository;

import io.cstool.comply22.entity.EntityVersion;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

public interface EntityVersionRepository extends Neo4jRepository<EntityVersion, Long>,
    NonDomainResults {

    /**
     * Finds most recent version of entity in the given timeline, set the "validUntil" timestamp of its pointer to 'now'
     * and set "validFrom" on the pointer of the new version to the same 'now'. Then move the current-pointer of
     * the entity to the new version.
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
    EntityVersion mergeVersionWithEntity(String reality, Long perpetualEntityId, Long newVersionId);

    @Transactional(propagation = REQUIRED)
    @Query("")
    EntityVersion mergeInitialVersion(String timeline, Long entityId, Long versionId);
}