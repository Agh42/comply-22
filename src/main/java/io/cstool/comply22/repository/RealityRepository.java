package io.cstool.comply22.repository;

import io.cstool.comply22.entity.Reality;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Set;

public interface RealityRepository extends Neo4jRepository<Reality, Long>,
    NonDomainResults {


    Set<Reality> findByName(String name);
}