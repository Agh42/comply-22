package io.cstool.comply22.entity.relations;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.cstool.comply22.adapter.DynPropsSerializer;
import io.cstool.comply22.entity.PerpetualEntity;
import io.cstool.comply22.entity.PerpetualEntityRef;
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
    private Long id;

    @TargetNode
    @JsonIgnore
    private PerpetualEntity targetNode;

    private PerpetualEntityRef target;

    @JsonProperty
    public PerpetualEntityRef getTarget() {
        return PerpetualEntityRef.of(targetNode);
    }

    private Instant from;

    private Instant until;

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