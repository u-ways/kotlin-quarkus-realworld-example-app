package io.realworld.domain.article

import io.realworld.domain.comment.Comment
import io.realworld.domain.user.User
import io.realworld.support.factory.CommentFactory
import io.realworld.support.factory.UserFactory
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.junit.jupiter.api.Test

class ArticleTest {
    @Test
    fun `Verify the equals and hashCode contract`() {
        EqualsVerifier
            .configure().suppress(Warning.SURROGATE_KEY)
            .forClass(Article::class.java)
            .withPrefabValues(User::class.java, UserFactory.create(), UserFactory.create())
            .withPrefabValues(Comment::class.java, CommentFactory.create(id = 1), CommentFactory.create(id = 2))
            .verify()
    }
}
