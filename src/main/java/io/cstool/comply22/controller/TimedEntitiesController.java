package io.cstool.comply22.controller;

import io.cstool.comply22.dto.request.CreateEntityDto;
import io.cstool.comply22.dto.response.EntityVersionDto;
import io.cstool.comply22.entity.PerpetualEntity;
import io.cstool.comply22.entity.PerpetualEntityRef;
import io.cstool.comply22.service.PerpetualEntityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.Instant;

import static io.cstool.comply22.entity.Reality.timeLineOrDefault;

@CrossOrigin(origins = {"http://cstool.io", "http://comply-22.cstool.io", "https://comply-22.cstool.io",
        "http://localhost:3000"})
@RestController
@Validated
@RequestMapping("/api/v1/entities")
public class TimedEntitiesController {

    PerpetualEntityService entityService;

    public TimedEntitiesController(PerpetualEntityService service) {
        entityService = service;
    }

    @PostMapping("/{label}")
    public EntityVersionDto create(@PathVariable @NotEmpty String label,
                                   @RequestParam(required = false) @Nullable String timeline,
                                   @RequestParam(required = false) @Nullable Instant timestamp,
                                   @RequestBody @Valid CreateEntityDto dto) {
        return entityService.createEntity(label, timeline, timestamp, dto);
    }

    @PutMapping("/{label}/{id}")
    public void update(@RequestBody @Valid EntityVersionDto dto,
                       @RequestParam(required = false) @Nullable String timeline,
                       @RequestParam(required = false) @Nullable Instant timestamp) {
        entityService.updateEntity(timeline,
                dto.getEntity(),
                timestamp,
                dto.getVersion());
    }



    @DeleteMapping("/{label}")
    public void deleteAll(@PathVariable @NotEmpty  String label) {
        entityService.deleteAll(label);
    }

    @GetMapping("/{label}")
    public Slice<PerpetualEntity> findAll(@PathVariable @NotEmpty String label,
                                    @RequestParam(required = false, defaultValue = "20")
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
                                    @Pattern(regexp = "(^$|asc|desc|ASC|DESC)")
                                            String sortOrder) {

        if (sortOrder.equalsIgnoreCase("asc"))
            return entityService.findAllCurrent(label, PageRequest.of(page, size, Sort.by(sortBy).ascending()));

        return entityService.findAllCurrent(label, PageRequest.of(page, size, Sort.by(sortBy).descending()));

    }

    @GetMapping(value = {"/{label}/{id}/versions/{versionId}"})
    public EntityVersionDto getVersionById(
            @PathVariable
            @NotEmpty
                String label,
            @PathVariable
            @NotNull
                Long id,
            @PathVariable
            @NotNull
                Long versionId
    ) {
        return new EntityVersionDto(new PerpetualEntityRef(id),
                entityService.find(label, id, versionId));
    }

    /**
     * Request one version of an entity. Without additional parameters this will return the latest version.
     * A timestamp may be used to request a specific version.
     *
     * @param id            The ID for the entity (required)
     * @param timestamp     Request the version that was valid during this point in time (optional)
     */
    @GetMapping(value = {"/{label}/{id}"})
    public EntityVersionDto getVersion(
            @PathVariable
            @NotEmpty
                    String label,
            @PathVariable
            @NotNull
                    Long id,
            @RequestParam(value = "timestamp", required = false)
                    Instant timestamp,
            @RequestParam(value="timeline", required = false)
                    String timeline
    ) {
        if (timestamp != null) {
            // get point in time:
            return new EntityVersionDto(
                    new PerpetualEntityRef(id),
                            entityService.find(
                                    label,
                                    timeLineOrDefault(timeline),
                                    id,
                                    timestamp));
        } else {
            // get latest:
            return new EntityVersionDto(
                    new PerpetualEntityRef(id),
                    entityService.find(
                            label,
                            timeLineOrDefault(timeline),
                            id));
        }
    }

    //public Article getArticle(@PathVariable(required = false) Integer articleId) {


    
}
