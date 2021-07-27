package io.realworld.domain.profile

import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
@RegisterForReflection
open class FollowRelationshipKey(
    @Column
    open var userId: String = "",

    @Column
    open var followingId: String = "",
) : Serializable {
    override fun toString(): String = "FollowRelationshipKey($userId, $followingId)"

    final override fun hashCode(): Int = userId.hashCode() + followingId.hashCode()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FollowRelationshipKey) return false
        if (userId != other.userId) return false
        if (followingId != other.followingId) return false
        return true
    }
}
