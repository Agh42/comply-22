package io.cstool.comply22.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.cstool.comply22.entity.EntityVersion;
import lombok.Data;

/**
 * Represents the initial version of an entity in conversations with the API client.
 * Not used for updates (subsequent versions).
 */
@Data
public class CreateEntityDto {
    EntityVersion version;

    @JsonCreator
    public CreateEntityDto(@JsonProperty EntityVersion version) {
        this.version = version;
    }
}
