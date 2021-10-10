package io.cstool.comply22.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import groovy.json.JsonSlurper
import io.cstool.comply22.Comply22Application
import io.cstool.comply22.entity.EntityDto
import io.cstool.comply22.entity.PerpetualEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes = Comply22Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(["test", "dev", "local"])
class EntityITSpec extends Specification {

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
        response.versionOf[0].reality == 0
    }

    def "Create a new entity"() {
        when:
        def beforeCreation = Instant.now()
        Object response = newEntity("cONtrOl", "Name1")

        then:
        response.entity.id != null
        response.entity.customLabels == ["Control"]

        response.version.name == "Name1"
        response.version.id != null
        response.version.dynamicProperties.keyString == "value1"
        response.version.dynamicProperties.keyDate == testDate
        response.version.dynamicProperties.keyInt == 42
        response.version.dynamicProperties.keyDouble == 4.2
        //TODO also support: response.version.dynamicProperties.keyArray == ["one", "two", "three"]

        Instant.parse(response.entity.versionOf[0].from) < Instant.now()
        Instant.parse(response.entity.versionOf[0].from) > beforeCreation
        response.entity.versionOf[0].until == null
    }

    def "update an entity"() {
        given:
        def beforeCreation = Instant.now()
        def dto = newEntity("Control")
        Long id = dto.entity.id
        def json = restTemplate.getForObject("/api/v1/entities/control/" + id,
                ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())

        then:

    }

//    def "remove an entity"() {
//        // sets "until" on last version to now
//        // sets "until" on all incoming relations to now
//    }

    private Object newEntity(String label, String name) {
        def anchor = PerpetualEntity.newInstance("label1")
        def version = anchor.newVersion(name, "Abbr1", Map.of(
                "keyString", "value1",
                "keyDate", Instant.parse(testDate),
                "keyInt", 42,
                //"keyArray", new String[]{"one", "two", "three"},
                "keyDouble", new Double(4.2d)
        ))
        HttpEntity<EntityDto> request = new HttpEntity<>(new EntityDto(anchor, null, version))
        def json = restTemplate.postForObject("/api/v1/entities/${label}", request, ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())
        response
    }

    def cleanup() {
        restTemplate.delete("/api/v1/entities/control")
    }
}
