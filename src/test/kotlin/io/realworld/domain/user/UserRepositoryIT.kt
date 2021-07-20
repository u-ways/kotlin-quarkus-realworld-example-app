package io.realworld.domain.user

import io.quarkus.test.junit.QuarkusTest
import io.realworld.infrastructure.security.BCryptHashProvider
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.transaction.Transactional

@QuarkusTest
internal class UserRepositoryIT {
    @Inject
    private lateinit var repository: UserRepository

    @Inject
    private lateinit var hashProvider: BCryptHashProvider

    @Test
    @Transactional
    fun `given a valid user registration details, when registered, then correct entity should be persisted`() {
        val newUser = UserFactory.create().run {
            UserRegistrationRequest(username, email, password)
        }

        repository.register(newUser)

        val result = repository.findById(newUser.username)

        checkNotNull(result)

        assertEquals(newUser.username, result.username)
        assertEquals(newUser.email, result.email)
    }

    @Test
    @Transactional
    fun `given a valid user registration details, when registered, then password should be hashed correctly`() {
        val newUser = UserFactory.create().run {
            UserRegistrationRequest(username, email, password)
        }

        repository.register(newUser)

        val result = repository.findByEmail(newUser.email)

        checkNotNull(result)
        assertNotEquals(newUser.password, result.password)
        assertTrue(hashProvider.verify(newUser.password, result.password))
    }
}
