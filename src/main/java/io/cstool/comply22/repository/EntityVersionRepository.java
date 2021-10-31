package io.cstool.comply22.repository;

import io.cstool.comply22.entity.EntityVersion;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EntityVersionRepository extends Neo4jRepository<EntityVersion, Long>,
    NonDomainResults {


}