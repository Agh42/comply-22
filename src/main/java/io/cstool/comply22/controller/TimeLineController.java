package io.cstool.comply22.controller;

import io.cstool.comply22.entity.Change;
import io.cstool.comply22.service.TimelineService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@CrossOrigin(origins = {"http://cstool.io", "http://comply-22.cstool.io", "https://comply-22.cstool.io",
        "http://localhost:3000"})
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
}
