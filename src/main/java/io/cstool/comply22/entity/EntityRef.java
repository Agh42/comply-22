package io.cstool.comply22.entity;

import lombok.Value;

@Value
public class EntityRef {
    Long id;

    public static EntityRef of(PerpetualEntity targetNode) {
        return new EntityRef(targetNode.getId());
    }
}
