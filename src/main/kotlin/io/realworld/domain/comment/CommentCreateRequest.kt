package io.realworld.domain.comment

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.article.Article
import io.realworld.domain.user.User
import java.time.Instant.now
import java.util.UUID

@JsonRootName("comment")
@RegisterForReflection
data class CommentCreateRequest(
    @JsonProperty("body")
    val body: String,
) {
    fun toComment(slug: UUID, authorId: String) = Comment(
        body = body,
        createdAt = now(),
        updatedAt = now(),
        author = User(username = authorId),
        article = Article(slug = slug)
    )
}
