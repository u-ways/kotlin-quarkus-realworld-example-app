package io.realworld.domain.article

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.tag.Tag
import io.realworld.domain.user.User
import java.util.*
import java.util.UUID.randomUUID

@JsonRootName("article")
@RegisterForReflection
data class ArticleCreateRequest(
    @field:JsonProperty("title")
    val title: String,

    @field:JsonProperty("description")
    val description: String,

    @field:JsonProperty("body")
    val body: String,

    @field:JsonProperty("tagList")
    val tagList: List<String>? = null,
) {
    fun toArticle(authorId: String, articleId: UUID = randomUUID()) = Article(
        slug = articleId,
        title = title,
        description = description,
        body = body,
        author = User(username = authorId),
        tagList = tagList?.map { Tag(it) }?.toMutableList() ?: mutableListOf()
    )
}
