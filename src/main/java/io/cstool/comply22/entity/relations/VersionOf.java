package io.cstool.comply22.entity.relations;

import io.cstool.comply22.entity.EntityVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import static lombok.AccessLevel.PRIVATE;

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
@NoArgsConstructor(access = PRIVATE)
public class VersionOf {

    @Id
    @GeneratedValue
    Long id;

    @TargetNode
    EntityVersion entityVersion;

    // TODO add from/until fields from version to relation as well

    public static VersionOf relationshipTo(EntityVersion version) {
        return new VersionOf(null, version);
    }
}
