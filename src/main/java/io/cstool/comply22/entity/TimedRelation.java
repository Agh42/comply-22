package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.cstool.comply22.adapter.DynPropsSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.time.Instant;
import java.util.Map;

@RelationshipProperties
@Data
@AllArgsConstructor
public class TimedRelation {

    private static final DynPropsSerializer dynPropsSerializer = new DynPropsSerializer();

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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer reality = 0;

    @CompositeProperty
    @JsonIgnore
    Map<String, Object> dynamicProperties;


    @JsonGetter("dynamicProperties")
    public Map<String,Object> serializeCustomProperties() {
        return dynPropsSerializer.serialize(dynamicProperties);
    }

    @JsonSetter("dynamicProperties")
    public void deserializeCustomProperties(Map<String, Object> props) {
        dynamicProperties.clear();
        dynamicProperties.putAll(props);
    }
}
