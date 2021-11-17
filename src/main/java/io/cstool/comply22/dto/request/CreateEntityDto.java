package io.cstool.comply22.dto.request;

import io.cstool.comply22.entity.EntityVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the initial version of an entity in conversations with the API client.
 * Not used for updates (subsequent versions).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEntityDto {
    EntityVersion version;
}
