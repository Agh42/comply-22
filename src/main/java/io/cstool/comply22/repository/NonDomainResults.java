package io.cstool.comply22.repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface NonDomainResults {

    @Transactional(readOnly = true)
    public Collection<String> findLabelsForNode(Long id);
}
