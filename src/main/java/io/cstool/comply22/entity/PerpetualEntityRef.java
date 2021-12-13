package io.cstool.comply22.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class PerpetualEntityRef {

    private Long id;

    public static PerpetualEntityRef of(PerpetualEntity targetNode) {
        if (targetNode==null)
            return null;
        return new PerpetualEntityRef(targetNode.getId());
    }
}
