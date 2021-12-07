package io.cstool.comply22.service;

import io.cstool.comply22.entity.Change;
import io.cstool.comply22.entity.Reality;
import io.cstool.comply22.repository.ChangeRepository;
import io.cstool.comply22.repository.RealityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static io.cstool.comply22.entity.Change.ChangeType.ROOT;

@Service
@Slf4j
public class TimelineService {
    @Autowired
    RealityRepository realityRepository;

    @Autowired
    ChangeRepository changeRepository;

    /**
     * Initialize the mainstream timeline. The first change timestamp is set to be the earliest
     * possible point in time. This makes it possible to insert alternate timelines later that
     * start at any given time in the past.
     */
    public void initialize() {
        realityRepository.findByName(Reality.MAINSTREAM).stream().findFirst().ifPresentOrElse(
                reality -> log.info(String.format("Mainstream timeline was found. ID: %s, Name: %s, Begins: %s",
                        reality.getId(),
                        reality.getName(),
                        reality.getBeginsWith().getRecorded())),
                () -> {
                    realityRepository.initialize(Reality.MAINSTREAM,
                            ROOT,
                            Instant.MIN);
                    log.info("Mainstream timeline was created.");
                }
        );
    }

    public Change findById(Long id) {
        var change = changeRepository.findById(id).orElseThrow();
        return change;
    }
}
