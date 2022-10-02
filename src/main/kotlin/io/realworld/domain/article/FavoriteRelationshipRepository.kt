package io.realworld.domain.article

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Parameters
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class FavoriteRelationshipRepository : PanacheRepositoryBase<FavoriteRelationship, FavoriteRelationshipKey> {
    fun isFavorited(articleId: UUID, favoriteById: String): Boolean = count(
        query = "id.articleId = :articleId and id.favoriteById = :favoriteById",
        params = Parameters.with("articleId", articleId).and("favoriteById", favoriteById)
    ) > 0

    fun favoritedCount(articleId: UUID): Long = count(
        query = "id.articleId = :articleId",
        params = Parameters.with("articleId", articleId)
    )

    fun favorite(articleId: UUID, favoriteById: String): Unit = run {
        persist(FavoriteRelationship(id = FavoriteRelationshipKey(articleId, favoriteById)))
    }

    fun unFavorite(articleId: UUID, favoriteById: String): Boolean = run {
        deleteById(FavoriteRelationshipKey(articleId, favoriteById))
    }
}
