package io.cstool.comply22.repository;

import io.cstool.comply22.entity.EntityVersion;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

public interface EntityVersionRepository extends Neo4jRepository<EntityVersion, Long>,
    NonDomainResults {

    /**
     * Finds most recent version of entity in the given timeline, set its "validUntil" timestamp to now
     * and set "validFrom" on the new node to the same instant.
     */
    @Transactional(propagation = REQUIRED)
    @Query("MATCH p=(a:Entity)<-[:VERSION_OF]-(:Version)-[:RECORDED_ON|NEXT_CHANGE*]->(:Version)<-[:ENDS_WITH]-(r:Reality{name:$timeline}) " +
            "WHERE id(a) = $perpetualEntityId " +
            "WITH a,r, [n IN nodes(p) WHERE n:Version AND n.validUntil = null] AS v" +
            "FOREACH (vtip IN v | SET vtip.validUntil = datetime.transaction()) " +
            "WITH a,r,v " +
            "MATCH (nv:Version) " +
            "WHERE id(nv) = $newVersionId " +
            "SET nv.validFrom = datetime.transaction() "
    )
    // TODO test query
    EntityVersion addVersionToEntity(String timeline, Long perpetualEntityId, Long newVersionId);
}