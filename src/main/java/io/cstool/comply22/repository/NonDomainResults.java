package io.cstool.comply22.repository;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;

public interface NonDomainResults {

    @Transactional(readOnly = true)
    Collection<String> findLabelsForNode(Long versionId);

    @Transactional
    void deleteAllByLabel(String label);

    @Transactional
    void mergeWithTimeline(String timeline, Long changeId);

    /**
     * Adjust old/new version's timestamps and move the "current version" pointer of the entity forward in this timeline.
     */
    @Transactional
    void mergeVersionWithEntity(String reality, Long perpetualEntityId, Long newVersionId, Instant timestamp);

    /** Set the first version of an entity as its current version.
     */
    @Transactional
    void mergeNewVersionWithEntity(Long perpetualEntityId, Long newVersionId);

    /**
     * Initialize the named timeline with a first change of the given type.
     * If the creation of the timeline is not associated with an entity change, use the change
     * type {@code ChangeType.ROOT}.
     */
    void initializeTimeline(String timeline, String changeType, Instant timestamp);


}
