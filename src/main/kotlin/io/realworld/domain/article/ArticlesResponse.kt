package io.realworld.domain.article

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class ArticlesResponse(
    @field:JsonProperty("articles")
    val articles: List<ArticleResponse>,

    @field:JsonProperty("articlesCount")
    val articlesCount: Int,
) {
    companion object {
        @JvmStatic
        fun build(articles: List<ArticleResponse>): ArticlesResponse =
            ArticlesResponse(articles = articles, articlesCount = articles.count())
    }
}
