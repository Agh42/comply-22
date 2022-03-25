package io.cstool.comply22.repository;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;

public interface NonDomainResults {

    @Transactional(readOnly = true)
    Collection<String> findLabelsForNode(Long perpetualId);

    @Transactional
    void deleteAllByLabel(String label);

    @Transactional
    void mergeWithTimeline(String timeline, Long changeId);

    /**
     * Adjust old/new version's timestamps and move the "current version" pointer of the entity forward in this timeline.
     */
    @Transactional
    void mergeVersionWithEntity(String reality, Long perpetualEntityId, Long newVersionId, Instant timestamp);

    /** Set the very first version of a new entity as its current version.
     */
    @Transactional
    void mergeCurrentVersionWithNewEntity(Long perpetualEntityId, Long newVersionId);

    /**
     * Initialize the named timeline with a first change of the given type.
     * <p>
     * I.e. when updating a past version of an entity, this change creates a branching point into a new reality.
     * <p>
     * If the creation of the timeline is not associated with an entity change, use the change
     * type {@code ChangeType.ROOT}.
     */
    void initializeTimeline(String timeline, String explanation, String changeType, Instant timestamp);


}
