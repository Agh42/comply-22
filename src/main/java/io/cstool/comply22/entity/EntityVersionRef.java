package io.cstool.comply22.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class EntityVersionRef {
    private Long id;

    public static EntityVersionRef of(EntityVersion version) {
        if (version == null)
            return null;
        return new EntityVersionRef(version.getId());
    }
}
