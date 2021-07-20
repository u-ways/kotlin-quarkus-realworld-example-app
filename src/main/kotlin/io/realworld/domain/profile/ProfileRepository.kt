package io.realworld.domain.profile

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Parameters.with
import io.realworld.domain.exception.ProfileNotFoundException
import io.realworld.domain.user.UserRepository
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject

@ApplicationScoped
class ProfileRepository : PanacheRepositoryBase<FollowRelationship, FollowRelationshipKey> {
    @Inject
    @field:Default
    private lateinit var userRepository: UserRepository

    fun findProfile(subjectedUserId: String, loggedInUserId: String? = null): ProfileResponse =
        userRepository.findById(subjectedUserId)?.run {
            ProfileResponse.build(
                user = this,
                isFollowing = if (loggedInUserId == null) false else isFollowing(subjectedUserId, loggedInUserId)
            )
        } ?: throw ProfileNotFoundException()

    fun isFollowing(subjectedUserId: String, loggedInUserId: String): Boolean = count(
        query = "id.userId = :loggedInUserId and id.followingId = :subjectedUserId",
        params = with("loggedInUserId", loggedInUserId).and("subjectedUserId", subjectedUserId)
    ) > 0

    fun follow(userToFollowId: String, loggedInUserId: String): Unit = run {
        check(userRepository.exists(userToFollowId))
        persist(FollowRelationship(id = FollowRelationshipKey(loggedInUserId, userToFollowId)))
    }

    fun unfollow(userToUnfollowId: String, loggedInUserId: String): Boolean = run {
        check(userRepository.exists(userToUnfollowId))
        deleteById(FollowRelationshipKey(loggedInUserId, userToUnfollowId))
    }
}
