package io.cstool.comply22.repository;

import io.cstool.comply22.entity.Reality;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Set;

public interface RealityRepository extends Neo4jRepository<Reality, Long>,
    NonDomainResults {


    Set<Reality> findByName(String name);

    @Query("MERGE (r:Reality{name:$timeline})<-[:TIP_OF]-(c:Change{root:TRUE}) " +
            "WITH r,c " +
            "MERGE (r)-[:BEGINS_WITH]->(c)")
    void initialize(String timeline);


}