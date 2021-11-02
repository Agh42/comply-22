package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Reality {

    public static final String MAINSTREAM = "C-137";

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Human-readable name for this reality.
     */
    private final String name;

    @CreatedDate
    Instant beginning;

    /**
     * Is {@code null} only for mainstream timeline.
     */
    @Relationship(type = "BRANCH_OF", direction = OUTGOING)
    @JsonProperty(access = READ_ONLY)
    private Reality branchOf;

    @Relationship(type = "BEGINS_WITH", direction = OUTGOING)
    @JsonProperty(access = READ_ONLY)
    private Change beginsWith;

    @Relationship(type = "ENDS_WITH", direction = OUTGOING)
    @JsonProperty(access = READ_ONLY)
    private Change tip;


}
