package io.realworld.domain.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection

@JsonRootName("user")
@RegisterForReflection
data class UserUpdateRequest(
    @JsonProperty("username")
    val username: String? = null,

    @JsonProperty("email")
    val email: String? = null,

    @JsonProperty("password")
    val password: String? = null,

    @JsonProperty("bio")
    val bio: String? = null,

    @JsonProperty("image")
    val image: String? = null,
) {
    fun applyChangesTo(existingUser: User) = User(
        username = username ?: existingUser.username,
        email = email ?: existingUser.email,
        password = password ?: existingUser.password,
        bio = bio ?: existingUser.bio,
        image = image ?: existingUser.image
    )
}
