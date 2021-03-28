package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.time.Instant;

@RelationshipProperties
@Data
@AllArgsConstructor
public class TimedRelation {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @TargetNode
    @JsonIgnore
    private PerpetualEntity targetNode;

    private EntityRef target;

    @JsonProperty
    public EntityRef getTarget() {
        return EntityRef.of(targetNode);
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant from;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant until;

    /**
     * Dynamic properties.
     */
    private String propsJson;
}
