package io.cstool.comply22.controller;

import io.cstool.comply22.entity.EntityDto;
import io.cstool.comply22.repository.TimedRelationshipsRepository;
import io.cstool.comply22.service.TimedEntityService;
import io.cstool.comply22.service.TimedRelationshipsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = { "http://cstool.io", "http://comply-22.cstool.io", "https://comply-22.cstool.io",
         "http://localhost:3000" })
@RestController
@RequestMapping("/api/v1/relationships")
public class TimedRelationshipsController {

    TimedRelationshipsService relationshipsService;

    TimedRelationshipsController(TimedRelationshipsService service) {
        relationshipsService = service;
    }


}
