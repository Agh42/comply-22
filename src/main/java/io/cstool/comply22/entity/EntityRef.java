package io.cstool.comply22.entity;

import lombok.Value;

@Value
public class EntityRef {
    String id;

    public static EntityRef of(TimedEntityAnchor targetNode) {
        return new EntityRef(targetNode.getId());
    }
}
