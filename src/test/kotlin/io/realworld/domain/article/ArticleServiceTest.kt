package io.realworld.domain.article

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.realworld.domain.exception.ArticleNotFoundException
import io.realworld.domain.profile.FollowRelationshipRepository
import io.realworld.support.factory.ArticleFactory
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import java.util.*

@QuarkusTest
internal class ArticleServiceTest {
    @InjectMock
    private lateinit var repository: ArticleRepository

    @InjectMock
    private lateinit var favoriteRelationshipRepository: FavoriteRelationshipRepository

    @InjectMock
    private lateinit var followRelationshipRepository: FollowRelationshipRepository

    private lateinit var service: ArticleService

    @BeforeEach
    internal fun setUp() {
        service = ArticleService(
            repository,
            favoriteRelationshipRepository,
            followRelationshipRepository
        )
    }

    @Test
    fun `Given a existing article, when article requested by slug, then service should return correct ArticleResponse`() {
        val existingArticle = ArticleFactory.create()

        `when`(repository.findById(existingArticle.slug)).thenReturn(existingArticle)

        existingArticle.run {
            `when`(favoriteRelationshipRepository.favoritedCount(slug)).thenReturn(5)
        }

        val result = service.get(existingArticle.slug)

        result.run {
            assertEquals(
                ArticleResponse.build(
                    article = existingArticle,
                    favoritesCount = 5,
                    isFavorited = false,
                    isFollowing = false,
                ),
                result
            )
        }
    }

    @Test
    fun `Given a new article request, when service create article, then service should persist and return correct ArticleResponse`() {
        val loggedInUser = UserFactory.create()
        val articleRequest = ArticleFactory.create().run {
            ArticleCreateRequest(title, description, body, tagList.map { it.name })
        }

        val expectedEntity = articleRequest.toArticle(loggedInUser.username)
        val expectedResponse = ArticleResponse.build(expectedEntity)

        val actual = service.create(articleRequest, loggedInUser.username)

        assertEquals(expectedResponse.title, actual.title)
        assertEquals(expectedResponse.body, actual.body)
        assertEquals(expectedResponse.description, actual.description)
        assertEquals(expectedResponse.author.username, actual.author.username)

        verify(repository).persist(any<Article>())
    }

    @Test
    fun `Given an article update request for an article that does not exist, when service tries to update article, then service should throw ArticleNotFoundException`() {
        val invalidId = UUID.randomUUID()

        `when`(repository.findById(invalidId)).thenReturn(null)

        assertThrows<ArticleNotFoundException> {
            service.update(invalidId, ArticleUpdateRequest())
        }
    }

    @Test
    fun `Given an article update request with a new title, when service update article, then service should persist, change slug, and return correct ArticleResponse`() {
        val existingArticle = ArticleFactory.create()

        `when`(repository.findById(existingArticle.slug)).thenReturn(existingArticle)

        val updateRequest = existingArticle.run {
            ArticleUpdateRequest(title, description)
        }

        val expectedUpdatedEntity = updateRequest.applyChanges(existingArticle)
        val expectedResponse = ArticleResponse.build(expectedUpdatedEntity)

        val actual = service.update(existingArticle.slug, updateRequest)

        // When the title is updated, then a new slug should be generated
        assertNotEquals(existingArticle.slug, actual.slug)

        assertEquals(expectedResponse.title, actual.title)
        assertEquals(expectedResponse.body, actual.body)
        assertEquals(expectedResponse.description, actual.description)
        assertEquals(expectedResponse.author.username, actual.author.username)

        verify(repository).persist(any<Article>())
    }

    @Test
    fun `Given an article delete request for an article that does not exist, when service tries to delete article, then service should throw ArticleNotFoundException`() {
        val invalidId = UUID.randomUUID()

        `when`(repository.deleteById(invalidId)).thenReturn(false)

        assertThrows<ArticleNotFoundException> {
            service.delete(invalidId)
        }
    }

    @Test
    fun `Given a logged-in user that follows a set of authors, when feed is requested, then service should return correct ArticlesResponse`() {
        val loggedInUser = UserFactory.create()
        val articlesWithAuthorsLoggedInUserFollows = ArticleFactory.create(3)

        `when`(repository.findByTheAuthorsAUserFollows(userId = loggedInUser.username))
            .thenReturn(articlesWithAuthorsLoggedInUserFollows)

        articlesWithAuthorsLoggedInUserFollows.forEach {
            `when`(favoriteRelationshipRepository.favoritedCount(it.slug)).thenReturn(1)
            `when`(favoriteRelationshipRepository.isFavorited(it.slug, loggedInUser.username)).thenReturn(true)
            `when`(followRelationshipRepository.isFollowing(it.author.username, loggedInUser.username)).thenReturn(true)
        }

        val (articles, articlesCount) = service.feed(loggedInUserId = loggedInUser.username)

        assertEquals(articlesCount, 3)

        articles.forEachIndexed { index, articleResponse ->
            assertEquals(
                ArticleResponse.build(
                    article = articlesWithAuthorsLoggedInUserFollows[index],
                    favoritesCount = 1,
                    isFavorited = true,
                    isFollowing = true
                ),
                articleResponse
            )
        }
    }

    @Test
    fun `Given a logged-in user, when a favorite is queried to a non-existing article, repository should throw IllegalStateException`() {
        val loggedInUser = UserFactory.create()
        val subjectedArticleId = UUID.randomUUID()

        `when`(repository.exists(subjectedArticleId)).thenReturn(false)

        assertThrows<IllegalStateException> {
            service.favorite(subjectedArticleId, loggedInUser.username)
        }
    }

    @Test
    fun `Given a logged-in user, when an unfollow is queried to a non-existing article, repository should throw IllegalStateException`() {
        val loggedInUser = UserFactory.create()
        val subjectedArticleId = UUID.randomUUID()

        `when`(repository.exists(subjectedArticleId)).thenReturn(false)

        assertThrows<IllegalStateException> {
            service.unFavorite(subjectedArticleId, loggedInUser.username)
        }
    }
}
