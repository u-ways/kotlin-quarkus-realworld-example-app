package io.realworld.infrastructure.configuration

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
internal class ApplicationConfigurationTest {
    @Inject
    lateinit var objectMapper: ObjectMapper

    companion object {
        @JsonRootName("rootName")
        class DAO {
            @field:JsonProperty("fieldOne")
            val fieldOne: String = "fieldOne"
        }
    }

    @Test
    fun `objectMapper should wrap DAO with root name on serialization`() {
        val result = objectMapper.writeValueAsString(DAO())
        assertEquals("{\"rootName\":{\"fieldOne\":\"fieldOne\"}}", result)
    }

    @Test
    fun `objectMapper should be to deserialize json with wrapped root name`() {
        val serializedJson = "{\"rootName\":{\"fieldOne\":\"fieldOne\"}}"
        val dao = objectMapper.readValue<DAO>(serializedJson)
        assertEquals(dao.fieldOne, "fieldOne")
    }
}
