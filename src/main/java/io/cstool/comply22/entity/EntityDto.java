package io.cstool.comply22.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents one version of an entity in conversations with the API client.
 */
@Data
@AllArgsConstructor
public class EntityDto {
    PerpetualEntity entity;
    EntityVersion version;
}
