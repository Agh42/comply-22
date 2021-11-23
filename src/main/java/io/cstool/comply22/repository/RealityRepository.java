package io.cstool.comply22.repository;

import io.cstool.comply22.entity.Reality;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.time.Instant;
import java.util.Set;

public interface RealityRepository extends Neo4jRepository<Reality, Long>,
    NonDomainResults {


    Set<Reality> findByName(String name);

    /**
     * Initialize the named timeline with a first change of the given type.
     * If the creation of the timeline is not associated with an entity change, use the change
     * type {@code ChangeType.ROOT}.
     */
    @Query("MERGE (r:Reality{name:$timeline})<-[:TIP_OF]-(c:Change{type:$changeType}) " +
            "WITH r,c " +
            "MERGE (r)-[:BEGINS_WITH]->(c{recorded: $timestamp})")
    void initialize(String timeline, String changeType, Instant timestamp);


}