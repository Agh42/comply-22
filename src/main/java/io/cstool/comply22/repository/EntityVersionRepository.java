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
     * and set "validFrom" on the pointer of the new version to the same 'now'.
     *
     * Explanation of the query:
     * <ol>
     * <li>Find a path from the wanted entity to all versions that have a change on the wanted timeline
     * <li>From all changes on that path, find the version with "validUntil: null". This is the current version of
     * the entity in this timeline.
     * <li>
     * </ol>
     */
    @Transactional(propagation = REQUIRED)
    @Query( "MATCH p = (e:Entity)-[:CURRENT]->(v:Version)-[:RECORDED_ON|NEXT|TIP_OF*]->(r:Reality{name:$timeline}) " +
            "WHERE id(e) = $perpetualEntityId " +
            "RETURN p "
            //r:Reality{name:$timeline}
            //"WITH a,r, [n IN nodes(p) WHERE n:Version AND n.validUntil = null ] AS v" +
            //   "FOREACH (vtip IN v | SET vtip.validUntil = datetime.transaction()) " + // this will be only one vtip
    )
    // TODO test query
    EntityVersion mergeVersionWithEntity(String timeline, Long perpetualEntityId, Long newVersionId);

    // move tip forward:
//    MATCH (r1:Reality {id:"r1"})<-[t:TIP_OF]-(n)
//    MATCH (c5:Change {id:5})
//    DELETE t
//    MERGE (r1)<-[:TIP_OF]-(c5)

}