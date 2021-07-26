package io.realworld.domain.article

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.profile.ProfileResponse
import io.realworld.domain.tag.TagsResponse
import java.time.Instant

@JsonRootName("article")
@RegisterForReflection
data class ArticleResponse(
    @field:JsonProperty("slug")
    val slug: String,

    @field:JsonProperty("title")
    val title: String,

    @field:JsonProperty("description")
    val description: String,

    @field:JsonProperty("body")
    val body: String,

    @field:JsonProperty("tagList")
    val tagList: TagsResponse,

    @field:JsonProperty("createdAt")
    @field:JsonFormat(shape = STRING) // FIXME: pattern is failing to pass tests "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val createdAt: Instant,

    @field:JsonProperty("updatedAt")
    @field:JsonFormat(shape = STRING) // FIXME: pattern is failing to pass tests "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val updatedAt: Instant,

    @field:JsonProperty("favorited")
    val favorited: Boolean,

    @field:JsonProperty("favoritesCount")
    val favoritesCount: Long,

    @field:JsonProperty("author")
    val author: ProfileResponse,
) {
    companion object {
        @JvmStatic
        fun build(
            article: Article,
            favoritesCount: Long = 0,
            isFavorited: Boolean = false,
            isFollowing: Boolean = false
        ): ArticleResponse = ArticleResponse(
            slug = article.slug.toString(),
            title = article.title,
            description = article.description,
            body = article.body,
            tagList = TagsResponse.build(article.tagList),
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            favorited = isFavorited,
            favoritesCount = favoritesCount,
            author = ProfileResponse.build(article.author, isFollowing),
        )
    }
}
