package io.realworld.domain.article

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.tag.Tag
import java.util.UUID

@JsonRootName("article")
@RegisterForReflection
data class ArticleUpdateRequest(
    @JsonProperty("title")
    val title: String? = null,

    @JsonProperty("description")
    val description: String? = null,

    @JsonProperty("body")
    val body: String? = null,

    @JsonProperty("tagList")
    val tagList: List<String>? = null,
) {
    fun applyChangesTo(existingArticle: Article, newArticleId: UUID = UUID.randomUUID()) =
        Article(
            slug = title?.let { newArticleId } ?: existingArticle.slug,
            title = title ?: existingArticle.title,
            description = description ?: existingArticle.description,
            body = body ?: existingArticle.body,
            tagList = tagList?.map { Tag(it) }?.toMutableList() ?: existingArticle.tagList,
            createdAt = existingArticle.createdAt,
            updatedAt = existingArticle.updatedAt,
            author = existingArticle.author,
        )
}
