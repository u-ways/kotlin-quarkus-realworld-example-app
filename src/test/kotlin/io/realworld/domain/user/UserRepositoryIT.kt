package io.realworld.domain.user

import io.quarkus.test.junit.QuarkusTest
import io.realworld.domain.article.ArticleRepository
import io.realworld.domain.comment.Comment
import io.realworld.domain.comment.CommentRepository
import io.realworld.support.factory.ArticleFactory
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.transaction.Transactional

@QuarkusTest
internal class UserRepositoryIT {
    @Inject
    lateinit var repository: UserRepository
    @Inject
    lateinit var articleRepository: ArticleRepository
    @Inject
    lateinit var commentRepository: CommentRepository

    @Test
    @Transactional
    fun `Given an existing user, when a query by user's email is made, then correct entity should returned`() {
        val existingUser = UserFactory.create().apply(repository::persist)

        val result = repository.findByEmail(existingUser.email)

        checkNotNull(result)

        assertEquals(existingUser.username, result.username)
        assertEquals(existingUser.email, result.email)
    }

    @Test
    @Transactional
    fun `Given an existing user, when an exists query is made by user's username, then correct result should be returned`() {
        val existingUser = UserFactory.create().apply(repository::persist)

        assertFalse(repository.existsUsername("INVALID_USERNAME"))
        assertTrue(repository.existsUsername(existingUser.username))
    }

    @Test
    @Transactional
    fun `Given an existing user, when an exists query is made by user's email, then correct result should be returned`() {
        val existingUser = UserFactory.create().apply(repository::persist)

        assertFalse(repository.existsEmail("INVALID_EMAIL"))
        assertTrue(repository.existsEmail(existingUser.email))
    }

    @Test
    @Transactional
    fun `Given an existing user with created articles, when deleted, then repository should allow cascade delete operation to owned articles`() {
        val existingUser = UserFactory.create()
        val createdArticle = ArticleFactory.create(author = existingUser)

        repository.persistAndFlush(existingUser)
        articleRepository.persistAndFlush(createdArticle)

        repository.deleteById(existingUser.username)

        assertNull(repository.findById(existingUser.username))
    }

    @Test
    @Transactional
    fun `Given an existing user with created comment, when deleted, then repository should cascade delete operation to owned comment`() {
        val existingUser = UserFactory.create()
        val createdArticle = ArticleFactory.create(author = existingUser)
        val createdComment = Comment(article = createdArticle, author = existingUser)

        repository.persistAndFlush(existingUser)
        articleRepository.persistAndFlush(createdArticle)
        commentRepository.persistAndFlush(createdComment)

        repository.deleteById(existingUser.username)

        assertNull(repository.findById(existingUser.username))
    }
}
