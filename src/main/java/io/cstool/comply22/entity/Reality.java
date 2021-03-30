package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reality {
    @Id
    @GeneratedValue
    private Long id;

    Instant beginning;

    @Relationship(type = "BRANCH_OF", direction = OUTGOING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Reality branchOf;
}
