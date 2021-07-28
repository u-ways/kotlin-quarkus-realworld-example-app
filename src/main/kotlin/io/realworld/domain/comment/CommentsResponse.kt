package io.realworld.domain.comment

import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonValue
import io.quarkus.runtime.annotations.RegisterForReflection

@JsonRootName("comments")
@RegisterForReflection
data class CommentsResponse(
    @JsonValue
    val comments: List<CommentResponse>,
) {
    companion object {
        @JvmStatic
        fun build(comments: List<CommentResponse>) = CommentsResponse(comments)
    }
}
