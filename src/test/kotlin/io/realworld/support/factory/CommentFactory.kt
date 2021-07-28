package io.realworld.support.factory

import io.realworld.domain.article.Article
import io.realworld.domain.comment.Comment
import io.realworld.domain.user.User
import java.time.Instant
import java.time.Instant.now

class CommentFactory {
    companion object {
        /**
         * Creates a random comment.
         */
        fun create(
            // A default id of 0 is given to Comment entities as the ID generation is
            // managed by Hibernate. So if we generated an id here (i.e. `nextLong(0, MAX_VALUE)`)
            // Hibernate will think it is a detached entity, for example, when we try to persist
            // (e.g. It will throw "PersistentObjectException: detached entity passed to persist)
            id: Long = 0,
            body: String = "body",
            created: Instant = now(),
            updated: Instant = now(),
            author: User = UserFactory.create(),
            article: Article = ArticleFactory.create(),
        ): Comment = Comment(id, body, created, updated, author, article)

        /**
         * Creates X amount of comments.
         * The comments can share a similar article, and/or author.
         */
        fun create(
            amount: Int,
            author: User = UserFactory.create(),
            article: Article = ArticleFactory.create(),
        ): List<Comment> = (0 until amount).map { create(author = author, article = article) }
    }
}
