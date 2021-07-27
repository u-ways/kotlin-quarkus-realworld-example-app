package io.realworld.domain.profile

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.user.User

@JsonRootName("profile")
@RegisterForReflection
data class ProfileResponse(
    @JsonProperty("username")
    val username: String,

    @JsonProperty("bio")
    val bio: String,

    @JsonProperty("image")
    val image: String,

    @JsonProperty("following")
    val following: Boolean,
) {
    companion object {
        @JvmStatic
        fun build(user: User, isFollowing: Boolean): ProfileResponse = ProfileResponse(
            username = user.username,
            bio = user.bio,
            image = user.image,
            following = isFollowing
        )
    }
}
