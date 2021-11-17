package io.cstool.comply22.entity.relations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.cstool.comply22.entity.EntityVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

/**
 * Facilitates bitemporal storage of entity states.
 * <p>
 * Actual-time (from/until) is the time when the entity's actual state existed as expressed in this version.
 * <p>
 * Recorded-time is the time at which this particular version was recorded in the system.
 * <p>
 * What-if-scenario analysis and similar use-cases are supported by the reality tree which allows keeping multiple
 * parallel versions of the database for the same time period.
 */
@RelationshipProperties
@Data
@AllArgsConstructor
public class VersionOf {

    @Id
    @GeneratedValue
    @JsonProperty(access = READ_ONLY)
    Long id;

    @TargetNode
    @JsonProperty(access = READ_ONLY)
    EntityVersion entityVersion;

    public static VersionOf relationShipTo(EntityVersion version) {
        return new VersionOf(null, version);
    }
}
