package io.realworld.domain.profile

import io.quarkus.test.junit.QuarkusTest
import io.realworld.domain.exception.ProfileNotFoundException
import io.realworld.domain.user.UserRepository
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.transaction.Transactional

@QuarkusTest
internal class ProfileRepositoryIT {
    @Inject
    lateinit var repository: ProfileRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Test
    @Transactional
    fun `Given existing profile, when requested, then repository should return correct profile details`() {
        val loggedInUser = UserFactory.create()
        val existingUser = UserFactory.create()

        userRepository.persist(listOf(existingUser, loggedInUser))

        val result = repository.findProfile(existingUser.username, loggedInUser.username)

        assertEquals(existingUser.username, result.username)
        assertEquals(existingUser.bio, result.bio)
        assertEquals(existingUser.image, result.image)

        assertFalse(result.following)
    }

    @Test
    @Transactional
    fun `Given non-existent profile, when requested, then repository should throw ProfileNotFoundException`() {
        assertThrows<ProfileNotFoundException> {
            repository.findProfile("INVALID_ID")
        }
    }

    @Test
    @Transactional
    fun `Given logged-in user following an existing user, when a profile of the existing user is requested, then repository should return correct follow relationship details`() {
        val loggedInUser = UserFactory.create()
        val followedExistingUser = UserFactory.create()

        userRepository.persist(listOf(loggedInUser, followedExistingUser))

        assertFalse(repository.isFollowing(followedExistingUser.username, loggedInUser.username))

        repository.follow(followedExistingUser.username, loggedInUser.username)

        assertTrue(repository.isFollowing(followedExistingUser.username, loggedInUser.username))
    }

    @Test
    @Transactional
    fun `Given logged-in user following an existing user, when unfollowed, then repository should return correct follow relationship details`() {
        val loggedInUser = UserFactory.create()
        val followedExistingUser = UserFactory.create()

        userRepository.persist(listOf(loggedInUser, followedExistingUser))

        repository.follow(followedExistingUser.username, loggedInUser.username)
        repository.unfollow(followedExistingUser.username, loggedInUser.username)

        assertFalse(repository.isFollowing(followedExistingUser.username, loggedInUser.username))
    }

    @Test
    @Transactional
    fun `Given a logged-in user, when a follow is requested to a non-existing user, repository should throw UserNotFoundException`() {
        val loggedInUser = UserFactory.create()

        userRepository.persist(loggedInUser)

        assertThrows<IllegalStateException> {
            repository.follow("INVALID_ID", loggedInUser.username)
        }
    }

    @Test
    @Transactional
    fun `Given a logged-in user, when an unfollow is requested to a non-existing user, repository should throw UserNotFoundException`() {
        val loggedInUser = UserFactory.create()

        userRepository.persist(loggedInUser)

        assertThrows<IllegalStateException> {
            repository.unfollow("INVALID_ID", loggedInUser.username)
        }
    }
}
