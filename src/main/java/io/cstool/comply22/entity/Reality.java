package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reality {

    public static final String MAINSTREAM = "C-137";

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Human-readable name for this reality.
     */
    @JsonProperty(access = READ_ONLY)
    @EqualsAndHashCode.Include
    private String name;

    /**
     * Is {@code null} only for mainstream timeline.
     */
    @Relationship(type = "BRANCH_OF", direction = OUTGOING)
    @JsonProperty(access = READ_ONLY)
    // TODO replace with change ref?
    private Reality branchOf;

    @Relationship(type = "BEGINS_WITH", direction = OUTGOING)
    @JsonProperty(access = READ_ONLY)
    // TODO replace with change ref?
    private Change beginsWith;

    @Relationship(type = "TIP_OF", direction = INCOMING)
    @JsonProperty(access = READ_ONLY)
    // TODO replace with change ref?
    private Change tip;


}
