package io.cstool.comply22.service

import io.cstool.comply22.Comply22Application
import io.cstool.comply22.dto.request.CreateEntityDto
import io.cstool.comply22.entity.PerpetualEntity
import io.cstool.comply22.repository.ChangeRepository
import io.cstool.comply22.service.PerpetualEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.time.Instant

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK

@SpringBootTest(classes = Comply22Application.class, webEnvironment = MOCK)
@ActiveProfiles(["test", "dev", "local"])
class EntityServiceITSpec extends Specification {

    @Autowired
    PerpetualEntityService entityService;

    @Autowired
    ChangeRepository changeRepository;

    def "insert a new entity"() {
        when: "a new entity is saved"
        def beforeSave = Instant.now()
        def entity = PerpetualEntity.newInstance("control")
        def version = entity.newVersion("mycontrol", "ctrl", null)
        def dto = new CreateEntityDto(version)
        def result = entityService.createEntity("control", null, dto)
                .getVersion()
        def afterSave = Instant.now()

        then: "it was initialized correctly"
        with(result.change) {
            id != null
            id > 0
            lastModifiedBy == null
            recorded > beforeSave
            recorded < afterSave
            nextChange == null
            tipOf != null
        }
        with(result) {
            from > beforeSave
            from < afterSave
            until == null
            id != null
            id > 0
            name == "mycontrol"
            abbreviation == "ctrl"
            (!deleted)
        }
    }
}
