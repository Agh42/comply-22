package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PerpetualEntityRef {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    public static PerpetualEntityRef of(PerpetualEntity targetNode) {
        if (targetNode==null)
            return null;
        return new PerpetualEntityRef(targetNode.getId());
    }
}
