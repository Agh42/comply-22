package io.cstool.comply22.entity;

import lombok.Value;

@Value
public class PerpetualEntityRef {
    Long id;

    public static PerpetualEntityRef of(PerpetualEntity targetNode) {
        return new PerpetualEntityRef(targetNode.getId());
    }
}
