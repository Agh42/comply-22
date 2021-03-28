package io.cstool.comply22.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * Represents one version of an entity in conversations with the API client.
 */
@Data
@AllArgsConstructor
public class EntityDto {
    PerpetualEntity entity;
    Set<VersionOf> versionsOf;
    EntityVersion version;
}
