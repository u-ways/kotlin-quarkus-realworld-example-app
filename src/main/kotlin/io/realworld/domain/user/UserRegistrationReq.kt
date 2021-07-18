package io.realworld.domain.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.utils.ValidationMessages.Companion.EMAIL_MUST_BE_NOT_BLANK
import io.realworld.utils.ValidationMessages.Companion.PASSWORD_MUST_BE_NOT_BLANK
import io.realworld.utils.ValidationMessages.Companion.USERNAME_MUST_MATCH_PATTERN
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@JsonRootName("user")
@RegisterForReflection
data class UserRegistrationReq(
    @field:Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = USERNAME_MUST_MATCH_PATTERN)
    @field:JsonProperty("username")
    val username: String,

    @field:Email
    @field:NotBlank(message = EMAIL_MUST_BE_NOT_BLANK)
    @field:JsonProperty("email")
    val email: String,

    @field:NotBlank(message = PASSWORD_MUST_BE_NOT_BLANK)
    @field:JsonProperty("password")
    val password: String,
) {
    override fun toString(): String = "UserRegistrationReq($username, $email, $password)"
}
