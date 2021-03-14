package io.cstool.comply22.controller

import com.fasterxml.jackson.databind.JsonNode
import io.cstool.comply22.Comply22Application
import io.cstool.comply22.entity.EntityDto
import io.cstool.comply22.entity.TimedEntityAnchor
import io.cstool.comply22.entity.TimedEntityVersion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Profiles
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification
import wslite.rest.RESTClient
import wslite.rest.RESTClientException

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT


//@SpringBootTest(webEnvironment = RANDOM_PORT)

//@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = MyServer.class)
//@WebAppConfiguration
//@IntegrationTest

@SpringBootTest(classes = Comply22Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(["test", "dev", "local"])
class RestApiITSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

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
        def anchor = TimedEntityAnchor.newInstance(["Label1", "Label2"] as Set)
        def version = anchor.newVersion("Name1", "Abbr1", Map.of(
                "key1", "value1",
                "key2", "value2"
        ))
        HttpEntity<EntityDto> request = new HttpEntity<>(new EntityDto(anchor, version))
        def entityDto = restTemplate.postForObject("/api/v1/entities", request, EntityDto)


        then:
        entityDto.anchor.id != null
        entityDto.anchor.labels == ["Label1", "Label2"]
        entityDto.version.name == "Name1"

//        restTemplate.defaultAcceptHeader = ContentType.JSON
//        def path = "/"
//        def params = ["foo":1,"bar":2]
//        def response = client.post(path: path) {
//            type ContentType.JSON
//            json params
//        }
//        assert response.json?.data == params
    }
}
