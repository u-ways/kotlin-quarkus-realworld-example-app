package io.realworld.infrastructure.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.inject.Singleton

@Dependent
class ApplicationConfiguration {
    @Singleton
    @Produces
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        // Wrap objects with our JSON root name (i.e. `@JsonRootName("user")`) on serialization.
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE)
        // Wrap objects root on deserialization.
        objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
        // Class that registers capability of serializing java.time objects with the Jackson core.
        objectMapper.registerModule(JavaTimeModule())
        // Module that adds support for serialization/deserialization of Kotlin classes and data classes.
        objectMapper.registerModule(kotlinModule())
        return objectMapper
    }
}
