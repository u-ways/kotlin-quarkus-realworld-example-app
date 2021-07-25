package io.realworld.domain.article

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.tag.Tag
import java.util.*

@JsonRootName("article")
@RegisterForReflection
data class ArticleUpdateRequest(
    @field:JsonProperty("title")
    val title: String? = null,

    @field:JsonProperty("description")
    val description: String? = null,

    @field:JsonProperty("body")
    val body: String? = null,

    @field:JsonProperty("tagList")
    val tagList: List<String>? = null,
) {
    fun applyChanges(existingArticle: Article, newArticleId: UUID = UUID.randomUUID()) =
        existingArticle.copy(
            slug = title?.let { newArticleId } ?: existingArticle.slug,
            title = title ?: existingArticle.title,
            description = description ?: existingArticle.description,
            body = body ?: existingArticle.body,
            tagList = tagList?.map { Tag(it) }?.toMutableList() ?: existingArticle.tagList
        )
}
