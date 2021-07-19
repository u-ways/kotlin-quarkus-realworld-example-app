package io.realworld.domain.profile

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import io.realworld.domain.exception.UserNotFoundException
import io.realworld.domain.user.UserRepository
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject

@ApplicationScoped
class ProfileRepository : PanacheRepositoryBase<FollowRelationship, FollowRelationshipKey> {

    @Inject
    @field:Default
    lateinit var userRepository: UserRepository

    fun findProfile(loggedInUserId: String?, username: String): Profile? = userRepository.findById(username)?.run {
        Profile(
            username = username,
            bio = bio,
            image = image,
            following = if (loggedInUserId == null) false else isFollowing(loggedInUserId, username)
        )
    }

    fun isFollowing(loggedInUserId: String, subjectedUserId: String): Boolean =
        findById(FollowRelationshipKey(loggedInUserId, subjectedUserId)) != null

    fun follow(loggedInUserId: String, userToFollowId: String): Unit = persist(
        FollowRelationship(
            id = FollowRelationshipKey(loggedInUserId, userToFollowId),
            user = userRepository.findById(loggedInUserId) ?: throw UserNotFoundException(),
            following = userRepository.findById(userToFollowId) ?: throw UserNotFoundException(),
        )
    )

    fun unfollow(loggedInUserId: String, userToUnfollowId: String): Unit = delete(
        FollowRelationship(
            id = FollowRelationshipKey(loggedInUserId, userToUnfollowId),
            user = userRepository.findById(loggedInUserId) ?: throw UserNotFoundException(),
            following = userRepository.findById(userToUnfollowId) ?: throw UserNotFoundException(),
        )
    )
}
