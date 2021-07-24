package io.realworld.domain.user

import io.quarkus.test.junit.QuarkusTest
import io.realworld.support.factory.UserFactory
import io.realworld.utils.ValidationMessages.Companion.EMAIL_MUST_BE_NOT_BLANK
import io.realworld.utils.ValidationMessages.Companion.PASSWORD_MUST_BE_NOT_BLANK
import io.realworld.utils.ValidationMessages.Companion.USERNAME_MUST_MATCH_PATTERN
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.validation.Validator

@QuarkusTest
internal class UserIT {
    /**
     * Accessing the Validator.
     * See: https://quarkus.io/guides/validation#accessing-the-validator
     *
     * Alternatively, you can get the validator from a factory.
     * See: https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#_validating_constraints
     */
    @Inject
    internal lateinit var validator: Validator

    @Test
    fun `username must not have whitespace or special characters`() {
        val invalidUsername = "% invalid@Username ?!"
        val constraintViolations = validator.validate(
            UserFactory.create(username = invalidUsername, email = "valid@email.com")
        )

        assertEquals(1, constraintViolations.size)
        assertEquals(
            USERNAME_MUST_MATCH_PATTERN,
            constraintViolations.iterator().next().message
        )
    }

    @Test
    fun `username must not be blank`() {
        val blankUsername = ""
        val constraintViolations = validator.validate(
            UserFactory.create(username = blankUsername, email = "valid@email.com")
        )

        assertEquals(1, constraintViolations.size)
        assertEquals(
            USERNAME_MUST_MATCH_PATTERN,
            constraintViolations.iterator().next().message
        )
    }

    @Test
    fun `email must be have valid format`() {
        val invalidEmail = "invalid@email@com"
        val constraintViolations = validator.validate(
            UserFactory.create(email = invalidEmail)
        )

        assertEquals(1, constraintViolations.size)
    }

    @Test
    fun `email must not be blank`() {
        val blankEmail = ""
        val constraintViolations = validator.validate(
            UserFactory.create(email = blankEmail)
        )

        assertEquals(1, constraintViolations.size)
        assertEquals(
            EMAIL_MUST_BE_NOT_BLANK,
            constraintViolations.iterator().next().message
        )
    }

    @Test
    fun `password must not be blank`() {
        val blankPassword = ""
        val constraintViolations = validator.validate(
            UserFactory.create(password = blankPassword)
        )

        assertEquals(1, constraintViolations.size)
        assertEquals(
            PASSWORD_MUST_BE_NOT_BLANK,
            constraintViolations.iterator().next().message
        )
    }
}
