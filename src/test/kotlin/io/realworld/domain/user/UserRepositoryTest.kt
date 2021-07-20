package io.realworld.domain.user

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.realworld.domain.exception.EmailAlreadyExistsException
import io.realworld.domain.exception.UsernameAlreadyExistsException
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.any

@QuarkusTest
internal class UserRepositoryTest {
    @InjectMock(returnsDeepMocks = true)
    lateinit var repository: UserRepository

    @Test
    fun `given an existing username, when registered again, then UsernameAlreadyExistsException should be thrown`() {
        val existingUsername = "existingUsername"

        val newUser = UserFactory.create(username = existingUsername).run {
            UserRegistrationRequest(username, email, password)
        }

        `when`(repository.register(newUser)).thenCallRealMethod()
        `when`(repository.findById(newUser.username)).thenReturn(UserFactory.create(username = existingUsername))

        assertThrows<UsernameAlreadyExistsException> {
            repository.register(newUser)
        }

        verify(repository, never()).persist(any<User>())
    }

    @Test
    fun `given an existing email, when registered again, then EmailAlreadyExistsException should be thrown`() {
        val existingEmail = "existing@mail.com"

        val newUser = UserFactory.create(email = existingEmail).run {
            UserRegistrationRequest(username, email, password)
        }

        `when`(repository.register(newUser)).thenCallRealMethod()
        `when`(repository.findById(newUser.username)).thenReturn(null)
        `when`(repository.findByEmail(newUser.email)).thenReturn(UserFactory.create(email = existingEmail))

        assertThrows<EmailAlreadyExistsException> {
            repository.register(newUser)
        }

        verify(repository, never()).persist(any<User>())
    }

    @Test
    fun `given an existing user, when an update with valid details occur, then repository should only update new entity details`() {
        val existingUser = UserFactory.create()
        val userUpdateReq = existingUser.run {
            UserUpdateRequest(username = "newUsername", bio = "newBio")
        }

        `when`(repository.update(existingUser.username, userUpdateReq))
            .thenCallRealMethod()

        `when`(repository.findById(existingUser.username))
            .thenReturn(existingUser)

        `when`(repository.findById(userUpdateReq.username!!))
            .thenReturn(null)

        repository.update(existingUser.username, userUpdateReq)

        verify(repository).persist(
            existingUser.copy(username = userUpdateReq.username!!, bio = userUpdateReq.bio!!)
        )
    }

    @Test
    fun `given an existing user , when an update occurs with an existing username, then UsernameAlreadyExistsException should be thrown`() {
        val existingUser = UserFactory.create()
        val anotherExistingUser = UserFactory.create()
        val userUpdateReq = UserUpdateRequest(username = anotherExistingUser.username)

        `when`(repository.update(existingUser.username, userUpdateReq))
            .thenCallRealMethod()

        `when`(repository.findById(existingUser.username))
            .thenReturn(existingUser)

        `when`(repository.findById(anotherExistingUser.username))
            .thenReturn(anotherExistingUser)

        assertThrows<UsernameAlreadyExistsException> {
            repository.update(existingUser.username, userUpdateReq)
        }

        verify(repository, never()).persist(any<User>())
    }

    @Test
    fun `given an existing user , when an update occurs with an existing email, then EmailAlreadyExistsException should be thrown`() {
        val existingUser = UserFactory.create()
        val anotherExistingUser = UserFactory.create()
        val userUpdateReq = UserUpdateRequest(email = anotherExistingUser.email)

        `when`(repository.update(existingUser.username, userUpdateReq))
            .thenCallRealMethod()

        `when`(repository.findById(existingUser.username))
            .thenReturn(existingUser)

        `when`(repository.findByEmail(anotherExistingUser.email))
            .thenReturn(anotherExistingUser)

        assertThrows<EmailAlreadyExistsException> {
            repository.update(existingUser.username, userUpdateReq)
        }

        verify(repository, never()).persist(any<User>())
    }
}
