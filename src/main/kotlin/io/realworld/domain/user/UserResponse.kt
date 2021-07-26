package io.realworld.domain.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection

@JsonRootName("user")
@RegisterForReflection
data class UserResponse(
    @field:JsonProperty("username")
    val username: String,

    @field:JsonProperty("email")
    val email: String,

    @field:JsonProperty("token")
    val token: String,

    @field:JsonProperty("bio")
    val bio: String,

    @field:JsonProperty("image")
    val image: String,
) {
    companion object {
        @JvmStatic
        fun build(user: User, token: String): UserResponse = UserResponse(
            username = user.username,
            email = user.email,
            token = token,
            bio = user.bio,
            image = user.image,
        )
    }
}
