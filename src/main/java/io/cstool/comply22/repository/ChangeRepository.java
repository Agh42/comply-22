package io.cstool.comply22.repository;

import io.cstool.comply22.entity.Change;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ChangeRepository extends Neo4jRepository<Change, Long>,
    NonDomainResults {



}