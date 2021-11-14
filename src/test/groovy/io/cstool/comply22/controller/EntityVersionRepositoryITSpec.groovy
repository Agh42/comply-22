package io.cstool.comply22.controller

import io.cstool.comply22.Comply22Application
import io.cstool.comply22.entity.PerpetualEntity
import io.cstool.comply22.repository.EntityVersionRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK

@SpringBootTest(classes = Comply22Application.class, webEnvironment = MOCK)
@ActiveProfiles(["test", "dev", "local"])
class EntityVersionRepositoryITSpec {

    private PerpetualEntityRepository entityRepository
    private EntityVersionRepository versionRepository

    def "insert a new entity version"() {
        given: "an existing entity version"
        def anchor = PerpetualEntity.newInstance("TestEntity")
        entityRepository.save(anchor)

        def version1 = anchor.newVersion("version1", "v1", Map.of(
                "key1", "value1",
                "key2", "value2"))
        versionRepository.save(version1)

        when: "a new version is saved"
        def version2 = anchor.newVersion("version2", "v2", Map.of(
                "key3", "value3",
                "key4", "value4"
        ))
        versionRepository.save(version2)
        version2 = versionRepository.mergeVersionWithEntity(version2)

        then: "new version timestamps are set correctly"
        version2.get

        when: "the old version is queried"

        then: "its validUntil time matches the new version's validFrom time"


    }
}
