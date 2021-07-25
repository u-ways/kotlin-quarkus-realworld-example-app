package io.realworld.infrastructure.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.realworld.infrastructure.web.NoJsonRootWrap
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.inject.Singleton

@Dependent
class ApplicationConfiguration {
    @Singleton
    @Produces
    fun objectMapper() = ObjectMapper().apply {
        // Wrap objects with our JSON root name (i.e. `@JsonRootName("user")`) on serialization.
        enable(SerializationFeature.WRAP_ROOT_VALUE)
        // Wrap objects root on deserialization.
        enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
        // Class that registers capability of serializing java.time objects with the Jackson core.
        registerModule(JavaTimeModule())
        // Module that adds support for serialization/deserialization of Kotlin classes and data classes.
        registerModule(kotlinModule())
    }

    @Singleton
    @Produces
    @NoJsonRootWrap
    fun noJsonRootWrapObjectMapper() = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        registerModule(kotlinModule())
    }
}
