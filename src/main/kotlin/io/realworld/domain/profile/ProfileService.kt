package io.realworld.domain.profile

import io.realworld.domain.exception.ProfileNotFoundException
import io.realworld.domain.user.UserRepository
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class ProfileService(
    @Inject val userRepository: UserRepository,
    @Inject val followRelationshipRepository: FollowRelationshipRepository
) {
    fun findProfile(subjectedUserId: String, loggedInUserId: String? = null): ProfileResponse =
        userRepository.findById(subjectedUserId)?.run {
            ProfileResponse.build(
                user = this,
                isFollowing =
                if (loggedInUserId == null) false
                else followRelationshipRepository.isFollowing(subjectedUserId, loggedInUserId)
            )
        } ?: throw ProfileNotFoundException()

    @Transactional
    fun follow(userToFollowId: String, loggedInUserId: String) = run {
        check(userRepository.existsUsername(userToFollowId))
        followRelationshipRepository.persist(
            FollowRelationship(
                id = FollowRelationshipKey(
                    loggedInUserId,
                    userToFollowId
                )
            )
        )
    }

    @Transactional
    fun unfollow(userToUnfollowId: String, loggedInUserId: String): Boolean = run {
        check(userRepository.existsUsername(userToUnfollowId))
        followRelationshipRepository.deleteById(
            FollowRelationshipKey(
                userId = loggedInUserId,
                followingId = userToUnfollowId
            )
        )
    }
}
