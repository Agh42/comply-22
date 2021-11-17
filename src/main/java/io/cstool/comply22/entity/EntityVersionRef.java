package io.cstool.comply22.entity;

import lombok.Value;

@Value
public class EntityVersionRef {
    Long id;

    public static EntityVersionRef of(EntityVersion version) {
        return new EntityVersionRef(version.getId());
    }
}
