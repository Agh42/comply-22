package io.cstool.comply22.dto.response;

import io.cstool.comply22.entity.EntityVersion;
import io.cstool.comply22.entity.PerpetualEntityRef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a version of an entity in conversations with the API client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityVersionDto {
    PerpetualEntityRef entity;
    EntityVersion version;
}
