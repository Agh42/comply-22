package io.cstool.comply22.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.cstool.comply22.controller.TimeLineController;
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
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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
     * in the database. However, it may also be past timestamp specified by the user. This facilitates
     * recordings of past events.
     * <p>
     * There may be multiple changes with the same timestamp if they are recorded at the same time
     * (in the same transaction). The order in which they were saved is then still determined by
     * the {@code nextChange} pointer (see below).
     * <p>
     * If two changes belong to different timelines they may also have identical or overlapping
     * {@code recorded} timestamps.
     * <p>
     * See also:
     * <ul>
     * <li>{@link Change#transactionTime}
     * <li>{@link EntityVersion#getFrom()}
     * <li>{@link EntityVersion#getUntil()}
     * <li>{@link Change#getNextChange()}
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

    /**
     * The next change in any timeline on any entity or relation.
     *  <p>
     *  There may be multiple next changes if the entity splits off into different timelines.
     *  Only one will be accessible here. It is selected by timeline specified in the query.
     */
    @Relationship(type = "NEXT", direction = OUTGOING)
    @JsonIgnore
    @Nullable
    private Change nextChange;

    /**
     * The next change in any timeline on the same entity or relation.
     * <p>
     * There may be multiple next related changes if the entity splits off into different timelines.
     * Only one will be accessible here. It is selected by timeline specified in the query.
     */
    @Relationship(type = "NEXT_RELATED", direction = OUTGOING)
    @JsonIgnore
    private Set<Change> nextRelatedChanges = new HashSet<>();

    // TODO replace with set of all next chnges (see relatedChanges)
    @JsonGetter("next")
    private ChangeRef getNextChangeRef() {
        return ChangeRef.of(nextChange);
    }

    /**
     * All possible next related changes in all timelines.
     * <p>
     * To query the next related change in a particular timeline see {@code TimelineController}.
     *
     * @see TimeLineController#getNextRelatedChange(Long, String)
     */
    @JsonGetter("nextRelated")
    private Set<ChangeRef> getNextRelatedChangeRef() {
        return ChangeRef.of(nextRelatedChanges);
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
        if (!(o instanceof Change other))
            return false;
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

        /**
         * CRUD types for relation changes:
         */
        public static final String INSERT_REL = "INSERT_REL";
        public static final String UPDATE_REL = "UPDATE_REL";
        public static final String DELETE_REL = "DELETE_REL";

    }


}
