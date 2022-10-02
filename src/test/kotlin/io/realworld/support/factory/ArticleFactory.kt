package io.realworld.support.factory

import io.realworld.domain.article.Article
import io.realworld.domain.tag.Tag
import io.realworld.domain.user.User
import java.time.Instant
import java.time.Instant.now
import java.util.UUID
import java.util.UUID.randomUUID

class ArticleFactory {
    companion object {
        /**
         * Creates a random article.
         */
        fun create(
            slug: UUID = randomUUID(),
            title: String = "title",
            description: String = "description",
            body: String = "body",
            tag: MutableList<Tag> = mutableListOf(),
            created: Instant = now(),
            updated: Instant = now(),
            author: User = UserFactory.create(),
        ): Article = Article(slug, title, description, body, tag, created, updated, author)

        /**
         * Creates X amount of articles.
         * The articles can share similar tags, and author.
         */
        fun create(
            amount: Int,
            tags: MutableList<Tag> = mutableListOf(),
            author: User = UserFactory.create(),
        ): List<Article> = (0 until amount).map { create(tag = tags, author = author) }
    }
}
