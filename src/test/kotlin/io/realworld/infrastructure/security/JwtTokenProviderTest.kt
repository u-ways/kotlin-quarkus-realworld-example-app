package io.realworld.infrastructure.security

import io.realworld.support.factory.UserFactory
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant.now
import java.time.temporal.ChronoUnit
import java.util.*

internal class JwtTokenProviderTest {
    @Test
    fun `given user id, when token is created, then it should contain correct details`() {
        val issuer = "TEST"
        val secret = "secret"
        val expirationTimeInMinutes = 1L
        val jwtTokenProvider = JwtTokenProvider(issuer, secret, expirationTimeInMinutes)

        val user = UserFactory.create()
        val expectedExpirationDate = Date(now().plus(expirationTimeInMinutes, ChronoUnit.MINUTES).toEpochMilli())
        val token = jwtTokenProvider.create(user.username)

        jwtTokenProvider.verify(token).also {
            assertEquals(issuer, it.issuer)
            assertEquals(user.username, it.subject)
            assertThat(it.expiresAt, lessThanOrEqualTo(expectedExpirationDate))
        }
    }
}
