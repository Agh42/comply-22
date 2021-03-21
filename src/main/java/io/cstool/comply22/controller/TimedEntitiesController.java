package io.cstool.comply22.controller;

import io.cstool.comply22.entity.EntityDto;
import io.cstool.comply22.entity.TimedEntityAnchor;
import io.cstool.comply22.service.TimedEntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNullElse;

@CrossOrigin(origins = {"http://cstool.io", "http://comply-22.cstool.io", "https://comply-22.cstool.io",
        "http://localhost:3000"})
@RestController
@Validated
@RequestMapping("/api/v1/entities")
public class TimedEntitiesController {

    TimedEntityService entityService;

    public TimedEntitiesController(TimedEntityService service) {
        entityService = service;
    }

    @PostMapping
    public EntityDto create(@RequestBody @Valid EntityDto dto) {
        var result = entityService.createEntity(dto);
        return result;
    }

    @GetMapping
    public Slice<EntityDto> findAll(@RequestParam(required = false, defaultValue = "100")
                                    @Min(value = 10)
                                    @Max(value = 200)
                                            Integer size,
                                    @RequestParam(required = false, defaultValue = "0")
                                    @Min(value = 0)
                                            Integer page,
                                    @RequestParam(required = false, defaultValue = "name")
                                    @Size(max = 1024)
                                            String sortBy,
                                    @RequestParam(required = false, defaultValue = "asc")
                                    @Pattern(regexp = "[asc|desc|ASC|DESC]")
                                            String sortOrder) {

        if (sortOrder.equalsIgnoreCase("asc"))
            return entityService.find(PageRequest.of(page, size, Sort.by(sortBy).ascending()));

        return entityService.find(PageRequest.of(page, size, Sort.by(sortBy).descending()));

    }

    /**
     * Request one version of an entity. Without additional parameters this will return the latest version. A version
     * number or a timestamp may be used to request a specific version. If both version number and timestamp are
     * specified, the number will take precedence and the timestamp will be ignored.
     *
     * @param id            The ID for the entity (required)
     * @param versionNumber A specific version number (optional)
     * @param timestamp     Request the version that was valid during this point in time (optional)
     */
    @GetMapping(value = {"/{id}", "/{id}/{versionNumber}"})
    public EntityDto getVersion(
            @PathVariable
            @NotEmpty
                    String id,
            @PathVariable(required = false)
                    Integer versionNumber,
            @RequestParam(value = "timestamp", required = false)
                    Instant timestamp
    ) {
        if (versionNumber != null) {
            // get specific version:
            return entityService.find(id, versionNumber);
        } else if (timestamp != null) {
            // get point in time:
            return entityService.find(id, timestamp).orElse(null);
        } else {
            // get latest:
            return entityService.find(id);
        }
    }

    //public Article getArticle(@PathVariable(required = false) Integer articleId) {

//    @PutMapping("/{id}")
//    public void update(@RequestBody @Valid TimedEntityVersion)


}
