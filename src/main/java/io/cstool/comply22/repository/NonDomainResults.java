package io.cstool.comply22.repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface NonDomainResults {

    @Transactional(readOnly = true)
    Collection<String> findLabelsForNode(Long id);

    @Transactional
    void deleteAllByLabel(String label);

    @Transactional
    void mergeWithTimeline(String timeline, Long changeId);
}
