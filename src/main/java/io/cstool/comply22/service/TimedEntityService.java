package io.cstool.comply22.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cstool.comply22.entity.EntityDto;
import io.cstool.comply22.entity.TimedEntityAnchor;
import io.cstool.comply22.repository.TimedEntityAnchorRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class TimedEntityService {

    TimedEntityAnchorRepository anchorRepository;

    public TimedEntityService(TimedEntityAnchorRepository anchorRepository) {
        this.anchorRepository = anchorRepository;
    }

    public EntityDto createEntity(EntityDto dto) {
        try {
            var anchor = TimedEntityAnchor.newInstance(dto.getAnchor().getLabels());
            var version = anchor.newVersion(
                    dto.getVersion().getName(),
                    dto.getVersion().getAbbreviation(),
                    dto.getVersion().getProperties());
            anchor = anchorRepository.save(anchor);
            return new EntityDto(
                    anchor,
                    anchor.getVersion(0).orElseThrow());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find the latest version of an entity.
     */
    public EntityDto find(String id) {
        return anchorRepository.findLatestVersion(id).orElseThrow();
    }

    /**
     * Find a specific version of an entity
     * @param id
     * @param versionNumber
     * @return
     */
    public EntityDto find(String id, Integer versionNumber) {
        return anchorRepository.findSpecificVersion(id, versionNumber).orElseThrow();

    }

    public Optional<EntityDto> find(String id, Instant timestamp) {
        return anchorRepository.findVersionAt(id, timestamp);
    }

    public Slice<EntityDto> find(Pageable pageable) {
        return anchorRepository.findAllCurrent(pageable);
    }
}