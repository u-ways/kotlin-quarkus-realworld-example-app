package io.realworld.domain.profile

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Parameters.with
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class FollowRelationshipRepository : PanacheRepositoryBase<FollowRelationship, FollowRelationshipKey> {
    fun isFollowing(subjectedUserId: String, loggedInUserId: String): Boolean = count(
        query = "id.userId = :loggedInUserId and id.followingId = :subjectedUserId",
        params = with("loggedInUserId", loggedInUserId).and("subjectedUserId", subjectedUserId)
    ) > 0
}
