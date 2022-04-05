package io.cstool.comply22.controller;

import io.cstool.comply22.entity.Change;
import io.cstool.comply22.service.TimelineService;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Allows access to the timeline and its changes.
 */
@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@Validated
@RequestMapping("/api/v1/timelines")
public class TimeLineController {

    TimelineService timelineService;

    public TimeLineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @GetMapping("/{id}")
    public Change getChange(@PathVariable @NotNull Long id){
        return timelineService.findById(id);
    }

    @GetMapping("/{id}/nextRelated")
    public Change getNextRelatedChange(@PathVariable @NotNull Long id,
                                       @RequestParam(value="timeline", required = false)
                                               String timeline){
        // TODO retreive next related change in timeline (or default timeline)
        return null;
    }

    @GetMapping("/{id}/next")
    public Change getNextChange(@PathVariable @NotNull Long id,
                                       @RequestParam(value="timeline", required = false)
                                               String timeline){
        // TODO retreive next change in timeline (or default timeline)
        return null;
    }

    @GetMapping("/first")
    public Optional<Change> getFirstChange(
            @RequestParam(required = false) @Nullable String reality) {
        return timelineService.findFirstChange(reality);
    }
}
