package io.realworld.domain.profile

import io.quarkus.test.junit.QuarkusTest
import io.realworld.domain.user.UserRepository
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.transaction.Transactional

@QuarkusTest
internal class ProfileServiceIT {
    @Inject
    lateinit var service: ProfileService
    @Inject
    lateinit var followRelationshipRepository: FollowRelationshipRepository
    @Inject
    lateinit var userRepository: UserRepository

    @Test
    @Transactional
    fun `Given existing profile, when requested, then repository should return correct profile details`() {
        val loggedInUser = UserFactory.create()
        val existingUser = UserFactory.create()

        userRepository.persist(listOf(existingUser, loggedInUser))

        val result = service.findProfile(existingUser.username, loggedInUser.username)

        assertEquals(existingUser.username, result.username)
        assertEquals(existingUser.bio, result.bio)
        assertEquals(existingUser.image, result.image)

        assertFalse(result.following)
    }

    @Test
    @Transactional
    fun `Given logged-in user following an existing user, when a profile of the existing user is requested, then repository should return correct follow relationship details`() {
        val loggedInUser = UserFactory.create()
        val followedExistingUser = UserFactory.create()

        userRepository.persist(listOf(loggedInUser, followedExistingUser))

        assertFalse(followRelationshipRepository.isFollowing(followedExistingUser.username, loggedInUser.username))

        service.follow(followedExistingUser.username, loggedInUser.username)

        assertTrue(followRelationshipRepository.isFollowing(followedExistingUser.username, loggedInUser.username))
    }

    @Test
    @Transactional
    fun `Given logged-in user following an existing user, when unfollowed, then repository should return correct follow relationship details`() {
        val loggedInUser = UserFactory.create()
        val followedExistingUser = UserFactory.create()

        userRepository.persist(listOf(loggedInUser, followedExistingUser))

        service.follow(followedExistingUser.username, loggedInUser.username)
        service.unfollow(followedExistingUser.username, loggedInUser.username)

        assertFalse(followRelationshipRepository.isFollowing(followedExistingUser.username, loggedInUser.username))
    }
}
