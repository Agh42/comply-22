package io.cstool.comply22.entity;

import lombok.Value;

@Value
public class EntityVersionRef {
    Long id;

    public static EntityVersionRef of(EntityVersion version) {
        if (version == null)
            return null;
        return new EntityVersionRef(version.getId());
    }
}
