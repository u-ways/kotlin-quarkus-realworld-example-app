package io.realworld.domain.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection

@JsonRootName("user")
@RegisterForReflection
data class UserUpdateReq(
    @field:JsonProperty("username")
    val username: String? = null,

    @field:JsonProperty("email")
    val email: String? = null,

    @field:JsonProperty("password")
    val password: String? = null,

    @field:JsonProperty("bio")
    val bio: String? = null,

    @field:JsonProperty("image")
    val image: String? = null,
) {
    override fun toString(): String = "UserUpdateReq($username, $email, $password, $bio, $image)"
}
