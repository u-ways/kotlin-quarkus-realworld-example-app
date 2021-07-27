package io.realworld.domain.user

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.realworld.domain.exception.*
import io.realworld.infrastructure.security.BCryptHashProvider
import io.realworld.infrastructure.security.JwtTokenProvider
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.any

@QuarkusTest
internal class UserServiceTest {
    @InjectMock
    lateinit var repository: UserRepository
    @InjectMock
    lateinit var hashProvider: BCryptHashProvider
    @InjectMock
    lateinit var tokenProvider: JwtTokenProvider

    private lateinit var service: UserService

    @BeforeEach
    internal fun setUp() {
        service = UserService(repository, tokenProvider, hashProvider)
    }

    @Test
    fun `Given an invalid username, when a request is made to retrieve a user by invalid username, then UserNotFoundException should be thrown`() {
        val invalidId = "INVALID_ID"

        `when`(repository.findById(invalidId)).thenReturn(null)

        assertThrows<UserNotFoundException> {
            service.get(invalidId)
        }
    }

    @Test
    fun `Given an existing username, when registered again, then UsernameAlreadyExistsException should be thrown`() {
        val existingUsername = "existingUsername"
        val userRegistrationRequest = UserFactory.create().run {
            UserRegistrationRequest(username = existingUsername, email, password)
        }

        `when`(repository.existsUsername(existingUsername)).thenReturn(true)

        assertThrows<UsernameAlreadyExistsException> {
            service.register(userRegistrationRequest)
        }

        verify(repository, never()).persist(any<User>())
    }


    @Test
    fun `Given an existing email, when registered again, then EmailAlreadyExistsException should be thrown`() {
        val existingEmail = "existing@mail.com"
        val userRegistrationRequest = UserFactory.create().run {
            UserRegistrationRequest(username, existingEmail, password)
        }

        `when`(repository.existsEmail(existingEmail)).thenReturn(true)

        assertThrows<EmailAlreadyExistsException> {
            service.register(userRegistrationRequest)
        }

        verify(repository, never()).persist(any<User>())
    }

    @Test
    fun `Given a valid registration request, when an persisted, then password should be hashed and a token should be generated`() {
        val token = "GENERATED_TOKEN"
        val hashedPassword = "HASHED_PASSWORD"
        val validRegistrationRequest = UserFactory.create().run {
            UserRegistrationRequest(username, email, password)
        }

        `when`(repository.existsUsername(validRegistrationRequest.username)).thenReturn(false)
        `when`(repository.existsEmail(validRegistrationRequest.email)).thenReturn(false)
        `when`(hashProvider.hash(validRegistrationRequest.password)).thenReturn(hashedPassword)
        `when`(tokenProvider.create(any())).thenReturn(token)

        service.register(validRegistrationRequest)

        verify(hashProvider).hash(validRegistrationRequest.password)
        verify(tokenProvider).create(validRegistrationRequest.username)
        verify(repository).persist(any<User>())
    }

    @Test
    fun `Given invalid email, when login is requested, then UnregisteredEmailException should be thrown`() {
        val invalidEmail = "INVALID_EMAIL@email.com"
        val userLoginRequest = UserFactory.create().run {
            UserLoginRequest(invalidEmail, password)
        }

        `when`(repository.findByEmail(invalidEmail)).thenReturn(null)

        assertThrows<UnregisteredEmailException> {
            service.login(userLoginRequest)
        }
    }

    @Test
    fun `Given invalid login password details, when login is requested, then InvalidPasswordException should be thrown`() {
        val invalidPassword = "INVALID_PASSWORD"
        val requestedUser = UserFactory.create()
        val userLoginRequest = requestedUser.run {
            UserLoginRequest(email, invalidPassword)
        }

        `when`(repository.findByEmail(userLoginRequest.email)).thenReturn(requestedUser)
        `when`(hashProvider.verify(invalidPassword, requestedUser.password)).thenReturn(false)

        assertThrows<InvalidPasswordException> {
            service.login(userLoginRequest)
        }
    }

    @Test
    fun `Given valid login details, when login is requested, then a token should be generated`() {
        val token = "GENERATED_TOKEN"
        val existingUser = UserFactory.create()
        val userLoginRequest = existingUser.run {
            UserLoginRequest(email, password)
        }

        `when`(repository.findByEmail(userLoginRequest.email)).thenReturn(existingUser)
        `when`(hashProvider.verify(userLoginRequest.password, existingUser.password)).thenReturn(true)
        `when`(tokenProvider.create(any())).thenReturn(token)

        service.login(userLoginRequest)

        verify(tokenProvider).create(existingUser.username)
    }


    @Test
    fun `Given an existing user, when an update occurs with an existing user's username, then UsernameAlreadyExistsException should be thrown`() {
        val existingUser = UserFactory.create()
        val loggedInUser = UserFactory.create()
        val userUpdateReq = UserUpdateRequest(username = existingUser.username)

        `when`(repository.findById(any())).thenReturn(loggedInUser)
        `when`(repository.existsUsername(userUpdateReq.username!!)).thenReturn(true)

        assertThrows<UsernameAlreadyExistsException> {
            service.update(loggedInUser.username, userUpdateReq)
        }

        verify(repository, never()).persist(any<User>())
    }

    @Test
    fun `Given an existing user, when an update occurs with an existing email, then EmailAlreadyExistsException should be thrown`() {
        val existingUser = UserFactory.create()
        val loggedInUser = UserFactory.create()
        val userUpdateReq = UserUpdateRequest(email = existingUser.email)

        `when`(repository.findById(any())).thenReturn(loggedInUser)
        `when`(repository.existsEmail(userUpdateReq.email!!)).thenReturn(true)

        assertThrows<EmailAlreadyExistsException> {
            service.update(loggedInUser.username, userUpdateReq)
        }

        verify(repository, never()).persist(any<User>())
    }
}
