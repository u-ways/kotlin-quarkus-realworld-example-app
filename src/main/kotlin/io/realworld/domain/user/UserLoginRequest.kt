package io.realworld.domain.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection

@JsonRootName("user")
@RegisterForReflection
data class UserLoginRequest(
    @field:JsonProperty("email")
    val email: String,

    @field:JsonProperty("password")
    val password: String,
)
