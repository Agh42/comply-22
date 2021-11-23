package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Node
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reality {

    public static final String MAINSTREAM = "C-137";

    @Id
    @GeneratedValue
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Human-readable name for this reality.
     */
    @JsonProperty(access = READ_ONLY)
    @ToString.Include
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

    public static String timeLineOrDefault(String timeline) {
        timeline = requireNonNullElse(timeline, Reality.MAINSTREAM);
        return timeline.isBlank() ? Reality.MAINSTREAM : timeline;
    }
}
