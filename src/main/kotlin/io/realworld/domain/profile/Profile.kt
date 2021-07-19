package io.realworld.domain.profile

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection

@JsonRootName("profile")
@RegisterForReflection
data class Profile(
    @field:JsonProperty("username")
    var username: String = "",

    @field:JsonProperty("bio")
    var bio: String = "",

    @field:JsonProperty("image")
    var image: String = "",

    @field:JsonProperty("following")
    var following: Boolean = false,
) {
    override fun toString(): String = "Profile($username, ${bio.take(20)}..., $image, $following)"
}
