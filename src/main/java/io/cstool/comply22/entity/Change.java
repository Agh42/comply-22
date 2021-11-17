package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;

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
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Change {
    @Id
    @GeneratedValue()
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @LastModifiedBy
    @JsonProperty(access = READ_ONLY)
    @ToString.Include
    String lastModifiedBy;

    @CreatedDate
    @JsonProperty(access = READ_ONLY)
    @ToString.Include
    Instant recorded;

    @EqualsAndHashCode.Include
    private ChangeType type;

    @Relationship(type = "NEXT", direction = OUTGOING)
    @JsonIgnore
    private Change nextChange;

    @JsonProperty(access = READ_ONLY)
    private ChangeRef getNextChangeRef() {
        return ChangeRef.of(nextChange);
    }

    @Relationship(type = "TIP_OF", direction = OUTGOING)
    @JsonIgnore
    private Reality tipOf;

    @JsonProperty(access = READ_ONLY)
    private RealityRef getTipOfRef() {
        return RealityRef.of(tipOf);
    }

    public enum ChangeType {
        /**
         * The mainstream timeline starts with one node of type ROOT. There is exactly one node of type in the entire
         * reality tree.
         */
        ROOT,

        /**
         * CRUD types used for entity version changes:
         */
        INSERT,
        UPDATE,
        DELETE
    }
}
