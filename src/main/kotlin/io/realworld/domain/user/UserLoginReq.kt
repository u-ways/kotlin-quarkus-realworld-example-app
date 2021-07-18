package io.realworld.domain.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.utils.ValidationMessages.Companion.EMAIL_MUST_BE_NOT_BLANK
import io.realworld.utils.ValidationMessages.Companion.PASSWORD_MUST_BE_NOT_BLANK
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@JsonRootName("user")
@RegisterForReflection
data class UserLoginReq(
    @field:Email
    @field:NotBlank(message = EMAIL_MUST_BE_NOT_BLANK)
    @field:JsonProperty("email")
    val email: String,

    @field:NotBlank(message = PASSWORD_MUST_BE_NOT_BLANK)
    @field:JsonProperty("password")
    val password: String,
) {
    override fun toString(): String = "UserLoginReq($email, $password)"
}
