package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

/**
 * A point in time that is associated with changes to one or more entities.
 */
@Node("Change")
@Data
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor(access = PRIVATE)
@ToString(onlyExplicitlyIncluded = true)
@Slf4j
public class Change {
    @Id
    @GeneratedValue
    @ToString.Include
    @JsonProperty(access = READ_ONLY)
    private Long id;

    @LastModifiedBy
    @JsonProperty(access = READ_ONLY)
    private String lastModifiedBy;

    Change(Instant recorded) {
        this.recorded = recorded;
        log.debug("Created new change, recorded: {}", recorded);
    }

    /**
     * The time at which a decision was made about a version. This may be the actual time when this change was recorded
     * in the database. However, it may also be past timestamp specified by the user. This is used to simulate
     * recordings of past events.
     * <p>
     * See also:
     * <ul>
     * <li>{@link Change#transactionTime}
     * <li>{@link EntityVersion#getFrom()}
     * <li>{@link EntityVersion#getUntil()}
     * </ul>
     *
     */
    @JsonProperty(access = READ_ONLY)
    @ToString.Include
    Instant recorded;

    /**
     * This always represents the actual time when this change was recorded to the database.
     */
    @CreatedDate
    @ToString.Include
    Instant transactionTime;

    @ToString.Include
    private String type;

    @Relationship(type = "NEXT", direction = OUTGOING)
    @JsonIgnore
    private Change nextChange;

    @JsonGetter("next")
    private ChangeRef getNextChangeRef() {
        return ChangeRef.of(nextChange);
    }

    @Relationship(type = "TIP_OF", direction = OUTGOING)
    @JsonIgnore
    private Reality tipOf;


    @JsonGetter("tipOf")
    public RealityRef getTipOfRef() {
        return RealityRef.of(tipOf);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Change))
            return false;
        Change other = (Change) o;
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Change.class.hashCode();
    }

    public static class ChangeType {
        /**
         * The mainstream timeline starts with one node of type ROOT. There is exactly one node of type in the entire
         * reality tree.
         */
        public static final String ROOT = "ROOT";

        /**
         * CRUD types used for entity version changes:
         */
        public static final String INSERT = "INSERT";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
    }


}
