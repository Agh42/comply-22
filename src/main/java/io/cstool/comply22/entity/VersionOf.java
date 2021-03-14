package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.With;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.Instant;
import java.util.UUID;

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
    TimedEntityVersion entityVersion;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Instant from;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Instant until;


    public static VersionOf relationShipTo(TimedEntityVersion version) {
        return new VersionOf(null, version, Instant.now(), null);
    }
}
