package io.realworld.domain.article

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.infrastructure.database.Tables.FAVORITED_RELATIONSHIP
import java.util.UUID.randomUUID
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = FAVORITED_RELATIONSHIP)
@RegisterForReflection
data class FavoriteRelationship(
    @EmbeddedId
    var id: FavoriteRelationshipKey = FavoriteRelationshipKey(randomUUID(), ""),
) : PanacheEntityBase {
    override fun toString(): String = "FavoriteRelationshipKey(${id.articleId}, ${id.favoriteById})"
}
