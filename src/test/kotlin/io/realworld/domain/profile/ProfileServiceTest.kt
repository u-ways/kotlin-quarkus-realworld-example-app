package io.realworld.domain.profile

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.realworld.domain.exception.ProfileNotFoundException
import io.realworld.domain.user.UserRepository
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`

@QuarkusTest
internal class ProfileServiceTest {
    @InjectMock
    lateinit var userRepository: UserRepository
    @InjectMock
    lateinit var followRelationshipRepository: FollowRelationshipRepository

    private lateinit var service: ProfileService

    @BeforeEach
    internal fun setUp() {
        service = ProfileService(
            userRepository,
            followRelationshipRepository
        )
    }

    @Test
    fun `Given non-existent profile, when requested, then repository should throw ProfileNotFoundException`() {
        `when`(userRepository.findById("INVALID_ID")).thenReturn(null)

        assertThrows<ProfileNotFoundException> {
            service.findProfile("INVALID_ID")
        }
    }

    @Test
    fun `Given a logged-in user, when a follow is requested to a non-existing user, repository should throw IllegalStateException`() {
        val loggedInUser = UserFactory.create()

        `when`(userRepository.existsUsername("INVALID_ID")).thenReturn(false)

        assertThrows<IllegalStateException> {
            service.follow("INVALID_ID", loggedInUser.username)
        }
    }

    @Test
    fun `Given a logged-in user, when an unfollow is requested to a non-existing user, repository should throw IllegalStateException`() {
        val loggedInUser = UserFactory.create()

        `when`(userRepository.existsUsername("INVALID_ID")).thenReturn(false)

        assertThrows<IllegalStateException> {
            service.unfollow("INVALID_ID", loggedInUser.username)
        }
    }
}
