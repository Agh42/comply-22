package io.cstool.comply22.entity;

import lombok.Value;

@Value
public class RealityRef {
    Long id;

    public static RealityRef of(Reality reality) {
        return new RealityRef(reality.getId());
    }
}
