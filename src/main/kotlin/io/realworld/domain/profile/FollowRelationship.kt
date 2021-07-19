package io.realworld.domain.profile

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.user.User
import javax.persistence.*

@Entity
@Table
@RegisterForReflection
data class FollowRelationship(
    @EmbeddedId
    var id: FollowRelationshipKey,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne
    @MapsId("followedById")
    @JoinColumn(name = "followedBy_id")
    var following: User,
) : PanacheEntityBase {
    override fun toString(): String = "FollowRelationship($id, ${user.username}, ${following.username})"
}
