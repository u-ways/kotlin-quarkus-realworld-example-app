package io.realworld.domain.article

import io.realworld.domain.exception.ArticleNotFoundException
import io.realworld.domain.profile.FollowRelationshipRepository
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val favoriteRelationshipRepository: FavoriteRelationshipRepository,
    private val followRelationshipRepository: FollowRelationshipRepository,
) {
    fun get(
        articleId: UUID,
        loggedInUserId: String? = null
    ): ArticleResponse = articleRepository
        .findById(articleId)
        ?.run {
            if (loggedInUserId != null) ArticleResponse.build(
                article = this,
                favoritesCount = favoriteRelationshipRepository.favoritedCount(slug),
                isFavorited = favoriteRelationshipRepository.isFavorited(slug, loggedInUserId),
                isFollowing = followRelationshipRepository.isFollowing(author.username, loggedInUserId)
            ) else ArticleResponse.build(
                article = this,
                favoritesCount = favoriteRelationshipRepository.favoritedCount(slug)
            )
        } ?: throw ArticleNotFoundException()

    fun create(
        createRequest: ArticleCreateRequest,
        loggedInUserId: String,
    ): ArticleResponse = createRequest
        .toEntity(loggedInUserId)
        .run {
            articleRepository.persist(this)
            ArticleResponse.build(this)
        }

    fun update(
        articleId: UUID,
        updateRequest: ArticleUpdateRequest
    ): ArticleResponse = updateRequest.applyChangesTo(
        existingArticle = articleRepository
            .findById(articleId) ?: throw ArticleNotFoundException()
    ).run {
        articleRepository.persist(this)
        ArticleResponse.build(this)
    }

    fun delete(
        articleId: UUID,
    ): Boolean = articleRepository
        .deleteById(articleId)
        .apply { if (!this) throw ArticleNotFoundException() }

    fun list(
        limit: Int = 20,
        offset: Int = 0,
        tags: List<String> = listOf(),
        authors: List<String> = listOf(),
        favorites: List<String> = listOf(),
        loggedInUserId: String? = null,
    ): ArticlesResponse = articleRepository
        .findBy(limit, offset, tags, authors, favorites)
        .run {
            ArticlesResponse.build(
                this.map {
                    if (loggedInUserId != null) ArticleResponse.build(
                        article = it,
                        favoritesCount = favoriteRelationshipRepository.favoritedCount(it.slug),
                        isFavorited = favoriteRelationshipRepository.isFavorited(it.slug, loggedInUserId),
                        isFollowing = followRelationshipRepository.isFollowing(it.author.username, loggedInUserId)
                    ) else ArticleResponse.build(
                        article = it,
                        favoritesCount = favoriteRelationshipRepository.favoritedCount(it.slug)
                    )
                }
            )
        }

    fun feed(
        limit: Int = 20,
        offset: Int = 0,
        loggedInUserId: String,
    ): ArticlesResponse = articleRepository
        .findByTheAuthorsAUserFollows(limit, offset, loggedInUserId)
        .run {
            ArticlesResponse.build(
                this.map {
                    ArticleResponse.build(
                        article = it,
                        favoritesCount = favoriteRelationshipRepository.favoritedCount(it.slug),
                        isFavorited = favoriteRelationshipRepository.isFavorited(it.slug, loggedInUserId),
                        isFollowing = followRelationshipRepository.isFollowing(it.author.username, loggedInUserId),
                    )
                }
            )
        }

    @Transactional
    fun favorite(
        articleId: UUID,
        loggedInUserId: String
    ): Unit = run {
        check(articleRepository.exists(articleId))
        favoriteRelationshipRepository.persist(
            FavoriteRelationship(
                id = FavoriteRelationshipKey(articleId, loggedInUserId)
            )
        )
    }

    @Transactional
    fun unFavorite(articleId: UUID, loggedInUserId: String): Boolean = run {
        check(articleRepository.exists(articleId))
        favoriteRelationshipRepository.deleteById(
            FavoriteRelationshipKey(articleId, loggedInUserId)
        )
    }

    fun isArticleAuthor(slug: UUID, username: String?): Boolean =
        if (username == null) false else articleRepository.exists(slug, username)
}
