package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static lombok.AccessLevel.PACKAGE;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

/**
 * A point in time that i associated with changes to one or more entities.
 */
@Node("Change")
@Data
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor(access = PACKAGE)
public class Change {
    @Id
    @GeneratedValue()
    private Long id;

    @LastModifiedBy
    @JsonProperty(access = READ_ONLY)
    String lastModifiedBy;

    @CreatedDate
    @JsonProperty(access = READ_ONLY)
    Instant recorded;

    ChangeType type;

    @Relationship(type = "NEXT", direction = OUTGOING)
    @JsonProperty(access = READ_ONLY)
    private Set<Change> nextChange;

    @Relationship(type = "TIP_OF", direction = OUTGOING)
    @JsonProperty(access = READ_ONLY)
    private Set<Reality> tipOf;

}
