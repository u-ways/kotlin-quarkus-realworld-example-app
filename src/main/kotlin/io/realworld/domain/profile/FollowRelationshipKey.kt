package io.realworld.domain.profile

import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
@RegisterForReflection
data class FollowRelationshipKey(
    @Column
    var userId: String = "",

    @Column
    var followingId: String = "",
) : Serializable {
    override fun toString(): String = "FollowRelationshipKey($userId, $followingId)"
}
