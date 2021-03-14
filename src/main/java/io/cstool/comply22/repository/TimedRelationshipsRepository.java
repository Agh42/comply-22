package io.cstool.comply22.repository;

import io.cstool.comply22.entity.TimedEntityAnchor;
import io.cstool.comply22.entity.TimedRelation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface TimedRelationshipsRepository extends Repository<TimedRelation, Long> {
 // cypher queries to add, change, remove relationships
}
