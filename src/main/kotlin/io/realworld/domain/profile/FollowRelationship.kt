package io.realworld.domain.profile

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.infrastructure.database.Tables.FOLLOW_RELATIONSHIP
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = FOLLOW_RELATIONSHIP)
@RegisterForReflection
open class FollowRelationship(
    @EmbeddedId
    var id: FollowRelationshipKey = FollowRelationshipKey("", ""),
) : PanacheEntityBase {
    override fun toString(): String = "FollowRelationship(${id.userId}, ${id.followingId})"

    final override fun hashCode(): Int = id.hashCode()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FollowRelationship) return false
        if (id != other.id) return false
        return true
    }
}
