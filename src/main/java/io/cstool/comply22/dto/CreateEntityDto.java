package io.cstool.comply22.dto;

import io.cstool.comply22.entity.EntityVersion;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the inital version of an entity in conversations with the API client.
 * Not used for updates (subsequent versions).
 */
@Data
@AllArgsConstructor
public class CreateEntityDto {
    EntityVersion version;
}
