package io.realworld.domain.article

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.profile.ProfileResponse
import io.realworld.domain.tag.TagsResponse
import java.time.Instant
import java.time.Instant.now

@JsonRootName("article")
@RegisterForReflection
data class ArticleResponse(
    @field:JsonProperty("slug")
    var slug: String = "",

    @field:JsonProperty("title")
    var title: String = "",

    @field:JsonProperty("description")
    var description: String = "",

    @field:JsonProperty("body")
    var body: String = "",

    @field:JsonProperty("tagList")
    var tagList: TagsResponse = TagsResponse(),

    @field:JsonProperty("createdAt")
    @field:JsonFormat(shape = STRING) // FIXME: pattern is failing to pass tests "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    var createdAt: Instant = now(),

    @field:JsonProperty("updatedAt")
    @field:JsonFormat(shape = STRING) // FIXME: pattern is failing to pass tests "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    var updatedAt: Instant = now(),

    @field:JsonProperty("favorited")
    var favorited: Boolean = false,

    @field:JsonProperty("favoritesCount")
    var favoritesCount: Long = 0,

    @field:JsonProperty("author")
    var author: ProfileResponse = ProfileResponse(),
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
