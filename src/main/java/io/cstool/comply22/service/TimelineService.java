package io.cstool.comply22.service;

import io.cstool.comply22.entity.Change;
import io.cstool.comply22.entity.Reality;
import io.cstool.comply22.repository.ChangeRepository;
import io.cstool.comply22.repository.RealityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.cstool.comply22.entity.Change.ChangeType.ROOT;

@Service
@Slf4j
public class TimelineService {
    @Autowired
    RealityRepository realityRepository;

    @Autowired
    ChangeRepository changeRepository;

    public void initialize() {
        realityRepository.findByName(Reality.MAINSTREAM).stream().findFirst().ifPresentOrElse(
                reality -> log.info(String.format("Mainstream timeline was found. ID: %s, Name: %s, Begins: %s",
                        reality.getId(),
                        reality.getName(),
                        reality.getBeginsWith().getRecorded())),
                () -> {
                    realityRepository.initialize(Reality.MAINSTREAM, ROOT);
                    log.info("Mainstream timeline was created.");
                }
        );
    }

    public Change findById(Long id) {
        return changeRepository.findById(id).orElseThrow();
    }
}
