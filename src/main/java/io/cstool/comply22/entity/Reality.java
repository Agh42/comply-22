package io.cstool.comply22.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

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
    public static final String MAINSTREAM_EXPLANATION = "The default reality (aka real life, aka Earth-1218).";

    @Id
    @GeneratedValue
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Human-readable name for this reality.
     */
    @ToString.Include
    private String name;

    /**
     * An optional description.
     */
    private String explanation;

    /**
     * Is {@code null} only for mainstream timeline.
     */
    @Relationship(type = "BRANCH_OF", direction = OUTGOING)
    // TODO replace with change ref?
    private Reality branchOf;

    @Relationship(type = "BEGINS_WITH", direction = OUTGOING)
    // TODO replace with change ref?
    private Change beginsWith;

    @Relationship(type = "TIP_OF", direction = INCOMING)
    // TODO replace with change ref?
    private Change tip;

    public static String timeLineOrDefault(String timeline) {
        timeline = requireNonNullElse(timeline, Reality.MAINSTREAM);
        return timeline.isBlank() ? Reality.MAINSTREAM : timeline;
    }

    public static boolean isMainstream(String timeline) {
        return timeline.equals(MAINSTREAM);
    }
}
