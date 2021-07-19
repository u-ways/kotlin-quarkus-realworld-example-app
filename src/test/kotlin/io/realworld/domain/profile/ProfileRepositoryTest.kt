package io.realworld.domain.profile

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.realworld.domain.exception.UserNotFoundException
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.never

@QuarkusTest
internal class ProfileRepositoryTest {
    @InjectMock(returnsDeepMocks = true)
    lateinit var repository: ProfileRepository

    @Test
    fun `Given a logged in user, when a profile is requested, repository should check if the logged in user follows that profile`() {
        val loggedInUser = UserFactory.create()
        val existingUser = UserFactory.create()

        `when`(repository.findProfile(loggedInUser.username, existingUser.username)).thenCallRealMethod()
        `when`(repository.userRepository.findById(existingUser.username)).thenReturn(existingUser)
        `when`(repository.isFollowing(loggedInUser.username, existingUser.username)).thenReturn(false)

        repository.findProfile(loggedInUser.username, existingUser.username)

        verify(repository).isFollowing(loggedInUser.username, existingUser.username)
    }

    @Test
    fun `Given a non-authenticated user, when a profile is requested, repository should not check if the logged in user follows that profile`() {
        val existingUser = UserFactory.create()

        `when`(repository.findProfile(null, existingUser.username)).thenCallRealMethod()
        `when`(repository.userRepository.findById(existingUser.username)).thenReturn(existingUser)

        repository.findProfile(null, existingUser.username)

        verify(repository, never()).isFollowing(any(), any())
    }

    @Test
    fun `Given a logged-in user, when a follow is requested to a non-existing user, repository should throw UserNotFoundException`() {
        val loggedInUser = UserFactory.create()

        `when`(repository.follow(loggedInUser.username, "INVALID_ID")).thenCallRealMethod()
        `when`(repository.userRepository.findById(loggedInUser.username)).thenReturn(loggedInUser)
        `when`(repository.userRepository.findById("INVALID_ID")).thenReturn(null)

        assertThrows<UserNotFoundException> {
            repository.follow(loggedInUser.username, "INVALID_ID")
        }
    }

    @Test
    fun `Given a logged-in user, when an unfollow is requested to a non-existing user, repository should throw UserNotFoundException`() {
        val loggedInUser = UserFactory.create()

        `when`(repository.unfollow(loggedInUser.username, "INVALID_ID")).thenCallRealMethod()
        `when`(repository.userRepository.findById(loggedInUser.username)).thenReturn(loggedInUser)
        `when`(repository.userRepository.findById("INVALID_ID")).thenReturn(null)

        assertThrows<UserNotFoundException> {
            repository.unfollow(loggedInUser.username, "INVALID_ID")
        }
    }
}
