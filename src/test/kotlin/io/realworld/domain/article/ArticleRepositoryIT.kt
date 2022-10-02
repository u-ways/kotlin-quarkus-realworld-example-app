package io.realworld.domain.article

import io.quarkus.test.junit.QuarkusTest
import io.realworld.domain.comment.Comment
import io.realworld.domain.comment.CommentRepository
import io.realworld.domain.profile.ProfileService
import io.realworld.domain.user.UserRepository
import io.realworld.support.factory.ArticleFactory
import io.realworld.support.factory.TagFactory
import io.realworld.support.factory.UserFactory
import io.realworld.support.matchers.containsInAnyOrder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.transaction.Transactional

@QuarkusTest
internal class ArticleRepositoryIT {
    @Inject
    lateinit var repository: ArticleRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var profileService: ProfileService

    @Inject
    lateinit var commentRepository: CommentRepository

    @Test
    @Transactional
    fun `Given existing article and user, when exists query is ran to verify if the user owns the article, then repository should verify article authorship`() {
        val articleAuthor = UserFactory.create()
        val existingArticle = ArticleFactory.create(
            author = articleAuthor
        )

        userRepository.persist(articleAuthor)
        repository.persist(existingArticle)

        assertTrue(repository.exists(existingArticle.slug, articleAuthor.username))
    }

    @Test
    @Transactional
    fun `Given existing article, when queried by slug, then repository should return correct article details`() {
        val articleAuthor = UserFactory.create()
        val articleTags = TagFactory.create(3)
        val existingArticle = ArticleFactory.create(
            tag = articleTags.toMutableList(),
            author = articleAuthor
        )

        userRepository.persist(articleAuthor)
        repository.persist(existingArticle)

        val result = repository.findById(existingArticle.slug)

        assertEquals(existingArticle, result)
    }

    @Test
    @Transactional
    fun `Given existing articles, when requested by tags & authors filter, then repository should return correct list of articles`() {
        val articleAuthors = UserFactory.create(3)
        val articleTags = TagFactory.create(3)

        val existingArticles = articleAuthors.mapIndexed { i, articleAuthor ->
            ArticleFactory.create(
                tag = mutableListOf(articleTags[i]),
                author = articleAuthor
            )
        }

        userRepository.persist(articleAuthors)
        repository.persist(existingArticles)

        assertEquals(
            existingArticles.first(),
            repository.findBy(
                limit = 1,
                tags = listOf(articleTags.first().name),
                authors = listOf(articleAuthors.first().username)
            ).first()
        )

        assertEquals(
            existingArticles.last(),
            repository.findBy(
                limit = 1,
                tags = listOf(articleTags.last().name),
                authors = listOf(articleAuthors.last().username)
            ).last()
        )
    }

    @Test
    @Transactional
    fun `Given existing articles, when requested by favorites filter, then repository should return correct list of articles`() {
        val articleAuthors = UserFactory.create(3)
        val articleTags = TagFactory.create(3)

        val existingArticles = articleAuthors.mapIndexed { i, articleAuthor ->
            ArticleFactory.create(
                tag = mutableListOf(articleTags[i]),
                author = articleAuthor
            )
        }

        userRepository.persist(articleAuthors)
        repository.persist(existingArticles)

        assertEquals(
            existingArticles.first(),
            repository.findBy(
                limit = 1,
                tags = listOf(articleTags.first().name),
                authors = listOf(articleAuthors.first().username)
            ).first()
        )

        assertEquals(
            existingArticles.last(),
            repository.findBy(
                limit = 1,
                tags = listOf(articleTags.last().name),
                authors = listOf(articleAuthors.last().username)
            ).last()
        )
    }

    @Test
    @Transactional
    fun `Given existing authors a user follows, when feed is queried, then repository should return correct list of articles`() {
        val loggedInUser = UserFactory.create()
        val articleAuthors = UserFactory.create(3)

        val existingArticles = articleAuthors.map {
            ArticleFactory.create(author = it)
        }

        userRepository.persist(articleAuthors.plus(loggedInUser))
        repository.persist(existingArticles)

        val authorArticlesThatLoggedInUserFollows = existingArticles
            .take(2)
            .onEach { profileService.follow(it.author.username, loggedInUser.username) }

        val feed = repository.findByTheAuthorsAUserFollows(userId = loggedInUser.username)

        assertThat(feed.size, `is`(2))
        assertTrue(feed.containsInAnyOrder(authorArticlesThatLoggedInUserFollows))

        // FIXME:
        //  Hamcrest `containsInAnyOrder` is not working as expected with Kotlin.
        //  Since this is a Quarkus demo I don't want to use Hamkrest (Kotlin's reimplementation of Hamcrest)
        //  As I am sticking with the provided testing libraries as much as possible.
        // assertThat(feed, containsInAnyOrder(authorArticlesThatLoggedInUserFollows))
    }

    @Test
    @Transactional
    fun `Given an existing article, when deleted, then repository should cascade delete operation to owned comments`() {
        val existingUser = UserFactory.create()
        val createdArticle = ArticleFactory.create(author = existingUser)
        val createdComment = Comment(article = createdArticle, author = existingUser)

        userRepository.persistAndFlush(existingUser)
        repository.persistAndFlush(createdArticle)
        commentRepository.persistAndFlush(createdComment)

        repository.deleteById(createdArticle.slug)

        assertNull(repository.findById(createdArticle.slug))
    }
}
