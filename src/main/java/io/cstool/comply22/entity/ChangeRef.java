package io.cstool.comply22.entity;

import lombok.Value;

@Value
public class ChangeRef {
    Long id;

    public static ChangeRef of(Change change) {
        return new ChangeRef(change.getId());
    }
}
