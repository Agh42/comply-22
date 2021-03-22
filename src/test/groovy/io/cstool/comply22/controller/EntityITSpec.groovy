package io.cstool.comply22.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import groovy.json.JsonSlurper
import io.cstool.comply22.Comply22Application
import io.cstool.comply22.entity.EntityDto
import io.cstool.comply22.entity.TimedEntityAnchor
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

    def "get a page of entities" () {
        when:
        def json = restTemplate.getForObject("/api/v1/entities", JsonNode)

        then:
        json != null
        json.content.isEmpty()

    }

    def "Get specific version"() {

    }

    def "Get version at point in time"() {

    }

    def "Get latest version of an entity"() {
        given:
        def beforeCreation = Instant.now()
        def entity = newEntity()
        String id = entity.anchor.id

        when:
        def json = restTemplate.getForObject("/api/v1/entities/" + id,
                ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())

        then:
        response.anchor.id != null
        response.anchor.labels == ["Label1", "Label2"]

        response.version.name == "Name1"
        response.version.dynamicProperties.keyString == "value1"
        response.version.dynamicProperties.keyDate == testDate
        response.version.dynamicProperties.keyInt == 42
        response.version.dynamicProperties.keyDouble == 4.2

        // TODO suport arrays (currently only first value will be saved)
        //response.version.dynamicProperties.keyArray == ["one", "two", "three"]

        Instant.parse(response.anchor.versionOf[0].from) < Instant.now()
        Instant.parse(response.anchor.versionOf[0].from) > beforeCreation
        response.anchor.versionOf[0].until == null
    }

    def "Create a new entity"() {
        when:
        def beforeCreation = Instant.now()
        Object response = newEntity()

        then:
        response.anchor.id != null
        response.anchor.labels == ["Label1", "Label2"]

        response.version.name == "Name1"
        response.version.id != null
        response.version.dynamicProperties.keyString == "value1"
        response.version.dynamicProperties.keyDate == testDate
        response.version.dynamicProperties.keyInt == 42
        response.version.dynamicProperties.keyDouble == 4.2
        //TODO also support: response.version.dynamicProperties.keyArray == ["one", "two", "three"]

        Instant.parse(response.anchor.versionOf[0].from) < Instant.now()
        Instant.parse(response.anchor.versionOf[0].from) > beforeCreation
        response.anchor.versionOf[0].until == null
    }

    def "update an entity"() {
        // saves a new version
    }

    def "remove an entity"() {
        // sets "until" on last version to now
        // sets "until" on all incoming relations to now
    }

    private Object newEntity() {
        def anchor = TimedEntityAnchor.newInstance(["Label1", "Label2"] as Set)
        def version = anchor.newVersion("Name1", "Abbr1", Map.of(
                "keyString", "value1",
                "keyDate", Instant.parse(testDate),
                "keyInt", 42,
                //"keyArray", new String[]{"one", "two", "three"},
                "keyDouble", new Double(4.2d)
        ))
        HttpEntity<EntityDto> request = new HttpEntity<>(new EntityDto(anchor, version))
        def json = restTemplate.postForObject("/api/v1/entities", request, ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())
        response
    }
}
