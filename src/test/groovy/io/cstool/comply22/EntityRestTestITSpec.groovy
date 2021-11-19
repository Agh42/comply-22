package io.cstool.comply22

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import groovy.json.JsonSlurper
import io.cstool.comply22.Comply22Application
import io.cstool.comply22.dto.request.CreateEntityDto
import io.cstool.comply22.dto.request.UpdateEntityDto
import io.cstool.comply22.entity.PerpetualEntity
import io.cstool.comply22.entity.Reality
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
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

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

    def "get a page of entities" () {
        given:
        37.times {
            newEntity("Control", "Name"+it)
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

//    def "Get specific version"() {
//
//    }

//    def "Get related entities at point in time"() 
//    def "Get related entities at current point in time"() 
//    def "Remove relation to another entity"()
//    def "Add relation to another entity"()
//    def "Update a relation's attributes"()
//    def "Get current relation attributes"()
//    def "Get relation attributes at point in time"()

//  def "Create alternate reality from now"()
//  def "Create alternate reality from point in time"()
//  def "Create alternate reality from alternate reality"()
//  def "Delete alternate reality"()

//    def "Get version at point in time"() {
//        given:
//        def beforeCreation = Instant.now()
//        def entity = newEntity()
//        String id = entity.entity.id
//        def beforeChange = Instant.now()
//        // TODO update, creating new version
//        def afterChange = Instant.now()
//
//        when: "get the last still valid version as of now"
//        def json = restTemplate.getForObject("/api/v1/entities/" + id + "?timestamp=" + Instant.now().toString(),
//                ObjectNode.class)
//        def response = jsonSlurper.parseText(json.toString())
//
//        then: "the version is retrieved"
//        response.entity.id != null
//        response.entity.labels == ["Label1", "Label2"]
//
//        response.version.name == "Name1"
//        response.version.dynamicProperties.keyString == "value1"
//        response.version.dynamicProperties.keyDate == testDate
//        response.version.dynamicProperties.keyInt == 42
//        response.version.dynamicProperties.keyDouble == 4.2
//    }

    def "Get latest version of an entity"() {
        given:
        def beforeCreation = Instant.now()
        def dto = newEntity("Control", "Name1")
        Long id = dto.entity.id

        when:
        def json = restTemplate.getForObject("/api/v1/entities/control/" + id,
                ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())

        then:
        response.id != null
        response.customLabels == ["Control"]
        response.versionOf[0].entityVersion.name == "Name1"
        response.versionOf[0].entityVersion.dynamicProperties.keyString == "value1"
        response.versionOf[0].entityVersion.dynamicProperties.keyDate == testDate
        response.versionOf[0].entityVersion.dynamicProperties.keyInt == 42
        response.versionOf[0].entityVersion.dynamicProperties.keyDouble == 4.2
        // TODO support arrays (currently only first value will be saved)
        //response.version.dynamicProperties.keyArray == ["one", "two", "three"]
        Instant.parse(response.versionOf[0].from) < Instant.now()
        Instant.parse(response.versionOf[0].from) > beforeCreation
        response.versionOf[0].until == null
        Instant.parse(response.versionOf[0].recorded) > beforeCreation
    }

    def "Create a new entity"() {
        when:
        def beforeCreation = Instant.now()
        Object version = newEntity("cONtrOl", "Name1").version

        then:
        version.name == "Name1"
        version.id != null
        version.abbreviation == "Abbr1"

        version.dynamicProperties.keyString == "value1"
        version.dynamicProperties.keyDate == testDate
        version.dynamicProperties.keyInt == 42
        version.dynamicProperties.keyDouble == 4.2

        Instant.parse(version.from) < Instant.now()
        Instant.parse(version.from) > beforeCreation
        version.until == null
        (!version.deleted)

        version.change != null
        version.change.id > 0

        when: "retreive the made change"
        def json = restTemplate.getForObject(
                String.format("/api/v1/timelines/%s", version.change.id),
                JsonNode)
        def changeResponse = jsonSlurper.parseText(json.toString())

        then: "the change was created correctly"
        with(changeResponse) {
            id > 0
            type == INSERT
            Instant.parse(recorded) > beforeCreation
            Instant.parse(recorded) < Instant.now()
            nextChange == null
            tipOf != null
        }


    }

    @Ignore
    def "Create a relation between entities"() {
        when:
        def beforeCreation = Instant.now()
        Object response1 = newEntity("cONtrOl", "Name1")
        Object response2 = newEntity("aSSet", "Name2")

        then:
        true == true

    }

    @Ignore
    def "update an entity"() {
        given:
        def beforeCreation = Instant.now()
        def dto = newEntity("Control", "MyControl")
        Long id = dto.entity.id
        def json = restTemplate.getForObject("/api/v1/entities/control/" + id,
                ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())

        // TODO xxx change GET dto to contain just the version. add /history to return the current anchor/versionOfs/version DTO

        // TODO on PUTting the version, follow link to anchor and insert new version, change from/until of last valid
        //  version (has until:null in this reality)

        // TODO use default reality if none specified as url parameter

        when: "the entity is modified"
        UpdateEntityDto updateDto = new UpdateEntityDto()
        updateDto.setEntityId(response.entity.id)
        updateDto.setReality(Reality.MAINSTREAM)
        updateDto.setEntityVersion(version)

        then: "a new version was saved"
        true == true
    }

//    def "remove an entity"() {
//        // sets "until" on last version to now
//        // sets "until" on all incoming relations to now
//    }

    private Object newEntity(String label, String name) {
        def anchor = PerpetualEntity.newInstance()
        def version = anchor.newVersion(name, "Abbr1", Map.of(
                "keyString", "value1",
                "keyDate", Instant.parse(testDate),
                "keyInt", 42,
                "keyDouble", new Double(4.2d)
        ))
        HttpEntity<CreateEntityDto> request = new HttpEntity<>(new CreateEntityDto(version))
        def json = restTemplate.postForObject("/api/v1/entities/${label}", request, ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())
        response
    }

    def cleanup() {
        restTemplate.delete("/api/v1/entities/control")
    }
}
