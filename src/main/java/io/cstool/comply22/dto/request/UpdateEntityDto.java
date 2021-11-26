package io.cstool.comply22.dto.request;

import io.cstool.comply22.entity.EntityVersion;
import io.cstool.comply22.entity.PerpetualEntityRef;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Save a new version for the given entity in the given reality.
 * Note: it is possible to "overwrite" (rather: skip) changes that were
 * made to the entity since the last version the caller retrieve.
 * Example:
 * - Retrieve entity version ev0
 * - Make changes to ev0
 * - Someone else also retrieves ev0 and saves a new version ev1
 * - You save your changes, creating ev2
 * - (You won't have seen ev1)
 *
 * The changes are not overwritten, but they won't be merged and you may not be
 * aware of changes that were made.
 */
@Data
@AllArgsConstructor
public class UpdateEntityDto {
    PerpetualEntityRef entity;
    EntityVersion version;
}
