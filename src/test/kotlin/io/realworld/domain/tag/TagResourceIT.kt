package io.realworld.domain.tag

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.realworld.support.factory.TagFactory
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import javax.inject.Inject
import javax.ws.rs.core.HttpHeaders.CONTENT_TYPE
import javax.ws.rs.core.HttpHeaders.LOCATION
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response.Status.CREATED
import javax.ws.rs.core.Response.Status.OK

/**
 * Testing a specific endpoints, see:
 * https://quarkus.io/guides/getting-started-testing#restassured
 */
@QuarkusTest
internal class TagResourceIT {
    @InjectMock
    lateinit var repository: TagRepository

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Test
    fun list() {
        val tagNames = TagFactory.create(2)

        `when`(repository.listAll())
            .thenReturn(tagNames)

        given()
            .accept(APPLICATION_JSON)
            .`when`()
            .get("/tags")
            .then()
            .body("size()", equalTo(1))
            // NOTE: Rest-Assured JsonPath implementation uses Groovy's GPath syntax.
            //  See https://github.com/rest-assured/rest-assured/wiki/Usage#json-using-jsonpath
            .body("tags", hasItems(tagNames.first().name, tagNames.last().name))
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(repository, times(1)).listAll()
    }

    @Test
    fun create() {
        val entity = TagFactory.create()

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(entity))
            .`when`()
            .post("/tags")
            .then()
            .header(LOCATION, containsString("/tags/${entity.name}"))
            .statusCode(CREATED.statusCode)

        verify(repository, times(1)).persist(entity)
    }
}
