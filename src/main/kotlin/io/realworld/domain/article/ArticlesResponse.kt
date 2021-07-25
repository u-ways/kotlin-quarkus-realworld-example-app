package io.realworld.domain.article

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class ArticlesResponse(
    @field:JsonProperty("articles")
    val articles: List<ArticleResponse> = emptyList(),

    @field:JsonProperty("articlesCount")
    var articlesCount: Int = 0,
) {
    companion object {
        @JvmStatic
        fun build(articles: List<ArticleResponse>): ArticlesResponse =
            ArticlesResponse(articles = articles, articlesCount = articles.count())
    }
}
