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
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import wslite.rest.RESTClient

import java.time.Instant

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes = Comply22Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(["test", "dev", "local"])
class RestApiITSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    JsonSlurper jsonSlurper

    def setupSpec() {
        jsonSlurper = new JsonSlurper()
    }

    def "get entities" () {
        when:
        def json = restTemplate.getForObject("/api/v1/entities", JsonNode)

        then:
        json != null
        json.content.isEmpty()

    }


    def "get current version of an entity" () {
        when:
        def entity = restTemplate.getForEntity('/api/v1/entities', EntityDto)

        then:
        entity != null
        entity.statusCode == HttpStatus.OK
        entity.body.anchor == null
        entity.body.version == null
    }

    def "Get entity"() {
        when:
        RESTClient client = new RESTClient("http://localhost:" + randomServerPort)
        def path = "/"
        def response
        response = client.get(path: path)

        then:
        assert response.statusCode == 200
        assert response.json?.headers?.host == "postman-echo.com"
    }

    def "Create a new entity"() {
        when:
        def beforeCreation = Instant.now()
        def anchor = TimedEntityAnchor.newInstance(["Label1", "Label2"] as Set)
        def version = anchor.newVersion("Name1", "Abbr1", Map.of(
                "key1", "value1",
                "key2", "value2"
        ))
        HttpEntity<EntityDto> request = new HttpEntity<>(new EntityDto(anchor, version))
        def json = restTemplate.postForObject("/api/v1/entities", request, ObjectNode.class)
        def response = jsonSlurper.parseText(json.toString())

        then:
        response.anchor.id != null
        response.anchor.labels == ["Label1", "Label2"]
        response.version.name == "Name1"
        Instant.parse(response.anchor.versionOf[0].from) < Instant.now()
        Instant.parse(response.anchor.versionOf[0].from) > beforeCreation
        response.anchor.versionOf[0].until == null
    }
}
