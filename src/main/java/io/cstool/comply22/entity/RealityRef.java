package io.cstool.comply22.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class RealityRef {
    private Long id;

    public static RealityRef of(Reality reality) {
        if (reality==null)
            return null;
        return new RealityRef(reality.getId());
    }
}
