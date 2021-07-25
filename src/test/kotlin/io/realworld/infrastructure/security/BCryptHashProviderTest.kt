package io.realworld.infrastructure.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class BCryptHashProviderTest {

    private lateinit var bCryptHashProvider: BCryptHashProvider

    @BeforeEach
    fun setUp() {
        bCryptHashProvider = BCryptHashProvider(5)
    }

    @Test
    fun `Given plaintext password, when hashed, then the password should not be the same`() {
        val plaintextPassword = "my password is my voice"
        val hashedPassword = bCryptHashProvider.hash(plaintextPassword)

        assertNotEquals(plaintextPassword, hashedPassword)
    }

    @Test
    fun `Given hashed password, when verified with invalid plaintext, then the provider should return a negative result`() {
        val plaintextPassword = "my password is my voice"
        val hashedPassword = bCryptHashProvider.hash(plaintextPassword)
        val result = bCryptHashProvider.verify("my password is my nose", hashedPassword)

        assertFalse(result)
    }

    @Test
    fun `Given hashed password, when verified with valid plaintext, then the provider should return a positive result`() {
        val plaintextPassword = "my password is my voice"
        val hashedPassword = bCryptHashProvider.hash(plaintextPassword)
        val result = bCryptHashProvider.verify(plaintextPassword, hashedPassword)

        assertTrue(result)
    }
}
