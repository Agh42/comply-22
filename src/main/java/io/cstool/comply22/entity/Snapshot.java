package io.cstool.comply22.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@Data
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Snapshot {

    @Id
    @GeneratedValue
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Human-readable name for this snapshot
     */
    @ToString.Include
    @NonNull
    private String name;

    /**
     * An optional description.
     */
    private String explanation;

    @Relationship(type = "OF_STATE", direction = OUTGOING)
    @NonNull
    private Change databaseState;

}
