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

    @Transactional
    void mergeVersionWithEntity(String reality, Long perpetualEntityId, Long newVersionId, Instant timestamp);

}
