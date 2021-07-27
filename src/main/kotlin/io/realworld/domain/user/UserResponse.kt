package io.realworld.domain.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection

@JsonRootName("user")
@RegisterForReflection
data class UserResponse(
    @JsonProperty("username")
    val username: String,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("token")
    val token: String,

    @JsonProperty("bio")
    val bio: String,

    @JsonProperty("image")
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
