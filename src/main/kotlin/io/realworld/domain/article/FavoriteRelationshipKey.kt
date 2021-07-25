package io.realworld.domain.article

import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.Serializable
import java.util.*
import java.util.UUID.randomUUID
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
@RegisterForReflection
data class FavoriteRelationshipKey(
    @Column
    var articleId: UUID = randomUUID(),

    @Column
    var favoriteById: String = "",
) : Serializable {
    override fun toString(): String = "FavoriteRelationshipKey($articleId, $favoriteById)"
}
