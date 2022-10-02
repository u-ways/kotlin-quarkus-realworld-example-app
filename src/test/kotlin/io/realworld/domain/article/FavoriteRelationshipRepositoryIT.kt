package io.realworld.domain.article

import io.quarkus.test.junit.QuarkusTest
import io.realworld.domain.user.UserRepository
import io.realworld.support.factory.ArticleFactory
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.transaction.Transactional

@QuarkusTest
internal class FavoriteRelationshipRepositoryIT {
    @Inject
    lateinit var articleRepository: ArticleRepository
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var favoriteRelationshipRepository: FavoriteRelationshipRepository

    @Test
    @Transactional
    fun `Given existing article, when user favorite the article, then isFavorited should return true`() {
        val existingUser = UserFactory.create()
        val existingArticle = ArticleFactory.create()

        userRepository.persist(listOf(existingUser, existingArticle.author))
        articleRepository.persist(existingArticle)

        assertFalse(favoriteRelationshipRepository.isFavorited(existingArticle.slug, existingUser.username))

        favoriteRelationshipRepository.favorite(existingArticle.slug, existingUser.username)

        assertTrue(favoriteRelationshipRepository.isFavorited(existingArticle.slug, existingUser.username))

        favoriteRelationshipRepository.unFavorite(existingArticle.slug, existingUser.username)

        assertFalse(favoriteRelationshipRepository.isFavorited(existingArticle.slug, existingUser.username))
    }

    @Test
    @Transactional
    fun `Given existing article favorite by 5 users, when favoritedCount is queried, then repository should return correct value`() {
        val existingUsers = UserFactory.create(5)
        val existingArticle = ArticleFactory.create()

        userRepository.persist(existingUsers.plus(existingArticle.author))
        articleRepository.persist(existingArticle)

        existingUsers.forEach {
            favoriteRelationshipRepository.favorite(existingArticle.slug, it.username)
        }

        assertEquals(5, favoriteRelationshipRepository.favoritedCount(existingArticle.slug))
    }
}
