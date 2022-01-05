package io.cstool.comply22

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import groovy.json.JsonSlurper
import io.cstool.comply22.dto.request.CreateEntityDto
import io.cstool.comply22.entity.Change
import io.cstool.comply22.entity.PerpetualEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant

import static io.cstool.comply22.entity.Change.ChangeType.INSERT
import static io.cstool.comply22.entity.Change.ChangeType.UPDATE
import static java.time.Instant.now
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

//  add /history to return the current anchor/versionOfs/version DTO
// TODO on PUTting the version, follow link to anchor and insert new version,
//  change from/until of last valid
//  version (has until:null in this reality)


@SpringBootTest(classes = Comply22Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(["test", "dev", "local"])
class EntityRestTestITSpec extends Specification {

    public static final String testDate = "1977-10-20T01:02:03Z"

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    JsonSlurper jsonSlurper

    def setupSpec() {
        jsonSlurper = new JsonSlurper()
    }

    def setup() {
        restTemplate.delete("/api/v1/entities/control")
    }

    def "get a page of entities"() {
        given:
        37.times {
            newEntity("Control", "Name" + it)
        }

        when:
        def json = restTemplate.getForObject("/api/v1/entities/control", JsonNode)
        def response = jsonSlurper.parseText(json.toString())

        then:
        response != null
        response.content.size() == 20
        response.number == 0
        response.size == 20
        response.numberOfElements == 20
        response.first == true
        response.last == false
        response.pageable.paged == true
        response.sort.sorted == true

        when: "get second page"
        json = restTemplate.getForObject("/api/v1/entities/control?page=1", JsonNode)
        response = jsonSlurper.parseText(json.toString())

        then:
        response != null
        response.content.size() == 17
        response.number == 1
        response.size == 20
        response.numberOfElements == 17
        response.first == false
        response.last == true
        response.pageable.paged == true
        response.sort.sorted == true
        response.empty == false

        when: "get nonexistent third page"
        json = restTemplate.getForObject("/api/v1/entities/control?page=2", JsonNode)
        response = jsonSlurper.parseText(json.toString())

        then:
        response.content.size() == 0
        response.pageable.offset == 40
        response.first == false
        response.last == true
        response.empty == true
    }

    @Ignore
    def "Get version at point in time"() {
        given:
        def beforeCreation = now()
        def entity = newEntity()
        String id = entity.entity.id
        def beforeChange = now()
        def afterChange = now()

        when: "get the last still valid version as of now"
        def json = restTemplate.getForObject("/api/v1/entities/" + id + "?timestamp=" + now().toString(),
                ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())

        then: "the version is retrieved"
        response.entity.id != null
        response.entity.labels == ["Label1", "Label2"]

        response.version.name == "Name1"
        response.version.dynamicProperties.keyString == "value1"
        response.version.dynamicProperties.keyDate == testDate
        response.version.dynamicProperties.keyInt == 42
        response.version.dynamicProperties.keyDouble == 4.2
    }

    def "Get latest version of an entity"() {
        given:
        def beforeCreation = now()
        def dto = newEntity("Control", "Name1")
        Long id = dto.entity.id

        when:
        def json = restTemplate.getForObject("/api/v1/entities/control/" + id,
                ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())
        def version = response.version

        then:
        version.name == "Name1"
        version.id != null
        version.abbreviation == "Abbr1"

        version.dynamicProperties.keyString == "value1"
        version.dynamicProperties.keyDate == testDate
        version.dynamicProperties.keyInt == 42
        version.dynamicProperties.keyDouble == 4.2

        Instant.parse(version.from) < now()
        Instant.parse(version.from) > beforeCreation
        version.until == null
        (!version.deleted)

        version.change != null
        version.change.id > 0
    }

    def "Find root change"(){
        when: "Request the root change of the default timeline"
        def json = restTemplate.getForObject(
                "/api/v1/timelines/first",
                JsonNode)
        def changeResponse = jsonSlurper.parseText(json.toString())

        then: "the change was found with correct values"
        with(changeResponse) {
            id > 0
            type == Change.ChangeType.ROOT
            Instant.parse(recorded) < now()
            Instant.parse(transactionTime) < now()
        }
    }

    def "Create a new entity"() {
        when:
        def beforeCreation = now()
        Object entity = newEntity("cONtrOl", "Name1")
        Object version = entity.version

        then:
        version.name == "Name1"
        version.id != null
        version.abbreviation == "Abbr1"

        version.dynamicProperties.keyString == "value1"
        version.dynamicProperties.keyDate == testDate
        version.dynamicProperties.keyInt == 42
        version.dynamicProperties.keyDouble == 4.2

        Instant.parse(version.from) < now()
        Instant.parse(version.from) > beforeCreation
        version.until == null
        (!version.deleted)

        version.change != null
        version.change.id > 0

        when: "retrieve the change that was made"
        def json = restTemplate.getForObject(
                String.format("/api/v1/timelines/%s", version.change.id),
                JsonNode)
        def changeResponse = jsonSlurper.parseText(json.toString())

        then: "the change was created correctly"
        with(changeResponse) {
            id > 0
            type == INSERT
            Instant.parse(recorded) > beforeCreation
            Instant.parse(recorded) < now()
            Instant.parse(transactionTime) > beforeCreation
            Instant.parse(transactionTime) < now()
            nextChange == null
            tipOf != null
        }
    }

    @Ignore
    def "Create a relation between entities"() {
        when:
        def beforeCreation = now()
        Object response1 = newEntity("cONtrOl", "Name1")
        Object response2 = newEntity("aSSet", "Name2")

        then:
        true == true

    }

    def "update an entity"() {
        given: "an entity"
        def beforeCreation = now()
        def label = "MyType"
        def dto = newEntity(label, "Name1")
        Long id = dto.entity.id

        when: "the initial version is retrieved"
        def json = restTemplate.getForObject("/api/v1/entities/mytype/" + id,
                JsonNode)
        def response = jsonSlurper.parseText(json.toString())
        def version = response.version
        def oldVersionId = response.version.id

        then: "all values are present"
        version.name == "Name1"
        version.id != null
        version.abbreviation == "Abbr1"

        version.dynamicProperties.keyString == "value1"
        version.dynamicProperties.keyDate == testDate
        version.dynamicProperties.keyInt == 42
        version.dynamicProperties.keyDouble == 4.2

        Instant.parse(version.from) < now()
        Instant.parse(version.from) > beforeCreation
        version.until == null
        (!version.deleted)

        version.change != null
        version.change.id > 0

        when: "its change is requested"
        def jsonChange = restTemplate.getForObject(
                String.format("/api/v1/timelines/%s", version.change.id),
                JsonNode)
        def changeResponse = jsonSlurper.parseText(jsonChange.toString())

        then: "all timestamps are set correctly"
        with (changeResponse) {
            lastModifiedBy == null
            Instant.parse(recorded) > beforeCreation
            Instant.parse(recorded) < now()
            Instant.parse(transactionTime) > beforeCreation
            Instant.parse(transactionTime) < now()
            nextChange == null
            type == INSERT
            tipOf.id != null
        }

        when: "the entity is modified"
        def beforeUpdate = now()
        response.version.name = "Changed name"

        //HttpEntity<JsonNode> request = new HttpEntity<>(response)
        restTemplate.put(
                String.format("/api/v1/entities/mytype/%s", response.entity.id),
                response)
        def afterUpdate = now()

        and: "the entity is requested again"
        json = restTemplate.getForObject(String.format("/api/v1/entities/mytype/%s",
                response.entity.id),
                JsonNode)
        def updatedResponse = jsonSlurper.parseText(json.toString())
        def updatedVersion = updatedResponse.version

        then: "a new version was saved"
        updatedResponse.entity.id == response.entity.id
        updatedVersion.id != null
        version.id != updatedVersion.id
        updatedVersion.name == "Changed name"
        updatedVersion.abbreviation == "Abbr1"
        updatedVersion.dynamicProperties.keyString == "value1"
        updatedVersion.dynamicProperties.keyDate == testDate
        updatedVersion.dynamicProperties.keyInt == 42
        updatedVersion.dynamicProperties.keyDouble == 4.2

        Instant.parse(updatedVersion.from) < now()
        Instant.parse(updatedVersion.from) > beforeCreation
        updatedVersion.until == null
        (!updatedVersion.deleted)

        updatedVersion.change != null
        updatedVersion.change.id > 0

        when: "the old version is requested"
        json = restTemplate.getForObject("/api/v1/entities/mytype/" + response.entity.id
                + "/versions/" + oldVersionId,
                JsonNode)
        def oldVersionResponse = jsonSlurper.parseText(json.toString())
        def oldVersion = oldVersionResponse.version

        then: "timestamps of the old version were modified"
        updatedVersion.from == oldVersion.until

        when: "the old and new change are requested"
        json = restTemplate.getForObject("/api/v1/timelines/" + version.change.id,
                JsonNode)
        def newChange = jsonSlurper.parseText(json.toString())

        json = restTemplate.getForObject("/api/v1/timelines/" + oldVersion.change.id,
                JsonNode)
        def oldChange = jsonSlurper.parseText(json.toString())

        then: "the timestamps are set correctly"
        with(newChange) {
            id == version.change.id
            lastModifiedBy == null
            Instant.parse(recorded) > Instant.parse(oldChange.recorded)
            Instant.parse(recorded) < now()
            Instant.parse(transactionTime) > Instant.parse(oldChange.transactionTime)
            Instant.parse(transactionTime) < now()
            nextChange == null
            type == UPDATE
        }

        and: "the new change is now the tip of the timeline"
        newChange.tipOf.id != null
        oldChange.tipOf.id == null

        and: "the old change points to the new change"
        oldChange.next.id == newChange.id
    }

    @Ignore
    def "remove an entity"() {
        // sets "until" on last version to now
        // sets "until" on all incoming relations to now
    }

    @Ignore
    def "Get specific version"() {}

    @Ignore
    def "Get related entities at point in time"() {}

    @Ignore
    def "Get related entities at current point in time"() {}

    @Ignore
    def "Remove relation to another entity"() {}

    @Ignore
    def "Add relation to another entity"() {}

    @Ignore
    def "Update a relation's attributes"() {}

    @Ignore
    def "Get current relation attributes"() {}

    @Ignore
    def "Get relation attributes at point in time"() {}

    @Ignore
    def "Create alternate reality from now"() {}

    @Ignore
    def "Create alternate reality from point in time"() {}

    @Ignore
    def "Create alternate reality from alternate reality"() {}

    @Ignore
    def "Updating a past version creates a new timeline"() {}

    @Ignore
    def "Cannot specify past time for mainstream reality"() {}

    @Ignore
    def "Delete alternate reality"() {}


    private Object newEntity(String label, String name) {
        def anchor = PerpetualEntity.newInstance()
        def version = anchor.insert(
                name,
                "Abbr1",
                Map.of(
                        "keyString", "value1",
                        "keyDate", Instant.parse(testDate),
                        "keyInt", 42,
                        "keyDouble", new Double(4.2d)
                ),
                now()
        )
        // FIXME json parse error: null createentitydto["version"]
        HttpEntity<CreateEntityDto> request = new HttpEntity<>(new CreateEntityDto(version))
        def json = restTemplate.postForObject("/api/v1/entities/${label}",
                request,
                JsonNode.class)
        def response = jsonSlurper.parseText(json.toString())
        response
    }

//    def cleanup() {
//        restTemplate.delete("/api/v1/entities/control")
//    }
}
