package io.realworld.domain.user

import io.realworld.domain.article.Article
import io.realworld.domain.comment.Comment
import io.realworld.support.factory.ArticleFactory
import io.realworld.support.factory.CommentFactory
import io.realworld.support.factory.UserFactory
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.jupiter.api.Test

internal class UserTest {
    @Test
    fun `Verify the equals and hashCode contract`() {
        EqualsVerifier
            .configure().suppress(Warning.SURROGATE_KEY)
            .forClass(User::class.java)
            .withPrefabValues(User::class.java, UserFactory.create(), UserFactory.create())
            .withPrefabValues(Article::class.java, ArticleFactory.create(), ArticleFactory.create())
            .withPrefabValues(Comment::class.java, CommentFactory.create(id = 1), CommentFactory.create(id = 2))
            .verify()
    }
}
