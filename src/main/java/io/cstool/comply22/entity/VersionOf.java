package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.Instant;

/**
 * Facilitates bitemporal storage of entity states.
 *
 * Actual-time (from/until) is the time when the entity's actual state existed as expressed
 * in this version.
 *
 * Recorded-time is the time at which this particular version was recorded in the system.
 *
 * What-if-scenario analysis and similar use-cases are supported by the reality tree which allows
 * keeping multiple parallel versions of the database for the same time period.
 */
@RelationshipProperties
@Data
@AllArgsConstructor
public class VersionOf {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @TargetNode
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    EntityVersion entityVersion;

    /**
     * Actual time when this verison
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Instant from;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Instant until;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Instant recorded;

    public static VersionOf relationShipTo(EntityVersion version) {
        return new VersionOf(null,
                version,
                Instant.now(),
                null,
                Instant.now()
        );
    }
}
