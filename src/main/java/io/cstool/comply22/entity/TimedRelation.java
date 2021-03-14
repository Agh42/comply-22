package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.With;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Indexed;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private TimedEntityAnchor targetNode;

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
