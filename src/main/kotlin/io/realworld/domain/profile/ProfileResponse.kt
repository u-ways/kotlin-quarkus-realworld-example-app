package io.realworld.domain.profile

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.user.User

@JsonRootName("profile")
@RegisterForReflection
data class ProfileResponse(
    @field:JsonProperty("username")
    val username: String,

    @field:JsonProperty("bio")
    val bio: String,

    @field:JsonProperty("image")
    val image: String,

    @field:JsonProperty("following")
    val following: Boolean,
) {
    companion object {
        @JvmStatic
        fun build(user: User, loggedInUser: User): ProfileResponse = build(
            user = user,
            isFollowing = loggedInUser.follows.firstOrNull { it == user } != null
        )

        @JvmStatic
        fun build(user: User, isFollowing: Boolean): ProfileResponse = ProfileResponse(
            username = user.username,
            bio = user.bio,
            image = user.image,
            following = isFollowing
        )
    }
}
