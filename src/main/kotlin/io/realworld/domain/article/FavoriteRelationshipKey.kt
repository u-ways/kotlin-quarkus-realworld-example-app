package io.realworld.domain.article

import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.Serializable
import java.util.*
import java.util.UUID.randomUUID
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
@RegisterForReflection
open class FavoriteRelationshipKey(
    @Column
    open var articleId: UUID = randomUUID(),

    @Column
    open var favoriteById: String = "",
) : Serializable {
    override fun toString(): String = "FavoriteRelationshipKey($articleId, $favoriteById)"

    final override fun hashCode(): Int = articleId.hashCode() + favoriteById.hashCode()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FavoriteRelationshipKey) return false
        if (articleId != other.articleId) return false
        if (favoriteById != other.favoriteById) return false
        return true
    }
}
