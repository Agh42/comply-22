package io.cstool.comply22.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

/**
 * Represents one version of an entity in conversations with the API client.
 */
@Data
@AllArgsConstructor
public class EntityDto {
    TimedEntityAnchor anchor;
    TimedEntityVersion version;
}
