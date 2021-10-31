package io.cstool.comply22.repository;

import io.cstool.comply22.entity.Change;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface ChangeRepository extends Neo4jRepository<Change, Long>,
    NonDomainResults {

    /**
     * Point reality tip to the new change. Link previous tip to new tip.
     *
     * @param timeline
     * @param changeId
     * @return
     */
    @Query("MATCH (r:Reality{name:$timeline})-[oew:ENDS_WITH]->(oldtip:Change) " +
            "WITH r,oew,oldtip " +
            "DELETE oew " +
            "WITH r,oldtip " +
            "MATCH (newtip:Change) " +
            "WHERE id(newtip) = $changeId " +
            "WITH r,oldtip,newtip " +
            "MERGE (oldtip)-[:NEXT_CHANGE]->(newtip)<-[:ENDS_WITH]-(r) " +
            "RETURN newtip"
    )
    Change mergeWithTimeline(String timeline, Long changeId);

}