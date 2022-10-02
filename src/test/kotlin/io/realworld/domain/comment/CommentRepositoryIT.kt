package io.realworld.domain.comment

import io.quarkus.test.junit.QuarkusTest
import io.realworld.domain.article.ArticleRepository
import io.realworld.domain.user.UserRepository
import io.realworld.support.factory.ArticleFactory
import io.realworld.support.factory.CommentFactory
import io.realworld.support.factory.UserFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.transaction.Transactional

@QuarkusTest
internal class CommentRepositoryIT {
    @Inject
    lateinit var repository: CommentRepository
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var articleRepository: ArticleRepository

    @Test
    @Transactional
    fun `Given an existing comment on an article, when findByArticle slug is invoked, then correct comment should be returned`() {
        val existingUser = UserFactory.create()
            .apply(userRepository::persist)
        val existingArticle = ArticleFactory.create(amount = 2, author = existingUser)
            .apply(articleRepository::persist)
        val existingComment = CommentFactory.create(article = existingArticle.first(), author = existingUser)
            .apply(repository::persist)

        // 1 extra unrelated comment
        CommentFactory.create(article = existingArticle.last(), author = existingUser)
            .apply(repository::persist)

        val result = repository.findByArticle(existingArticle.first().slug)

        assertThat(result, hasSize(1))
        assertEquals(result.first().id, existingComment.id)
    }

    @Test
    @Transactional
    fun `Given a comment made by an existing user, when exists is invoked to check that the comment belongs to that user, then it should return true`() {
        val existingUser = UserFactory.create()
            .apply(userRepository::persist)
        val existingArticle = ArticleFactory.create(amount = 2, author = existingUser)
            .apply(articleRepository::persist)
        val existingComment = CommentFactory.create(article = existingArticle.first(), author = existingUser)
            .apply(repository::persist)

        assertFalse(repository.exists(existingComment.id, "INVALID_USERNAME"))
        assertTrue(repository.exists(existingComment.id, existingUser.username))
    }
}
