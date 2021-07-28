package io.realworld.domain.comment

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.profile.ProfileResponse
import java.time.Instant

@JsonRootName("comment")
@RegisterForReflection
data class CommentResponse(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("body")
    val body: String,

    @JsonProperty("createdAt")
    val createdAt: Instant,

    @JsonProperty("updatedAt")
    val updatedAt: Instant,

    @JsonProperty("author")
    val author: ProfileResponse,
) {
    companion object {
        @JvmStatic
        fun build(comment: Comment, isFollowing: Boolean = false) = CommentResponse(
            comment.id,
            comment.body,
            comment.createdAt,
            comment.updatedAt,
            ProfileResponse.build(comment.author, isFollowing),
        )
    }
}
