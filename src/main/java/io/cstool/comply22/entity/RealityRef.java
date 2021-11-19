package io.cstool.comply22.entity;

import lombok.Value;

@Value
public class RealityRef {
    Long id;

    public static RealityRef of(Reality reality) {
        if (reality==null)
            return null;
        return new RealityRef(reality.getId());
    }
}
