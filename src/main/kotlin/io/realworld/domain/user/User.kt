package io.realworld.domain.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.utils.ValidationMessages.Companion.EMAIL_MUST_BE_NOT_BLANK
import io.realworld.utils.ValidationMessages.Companion.PASSWORD_MUST_BE_NOT_BLANK
import io.realworld.utils.ValidationMessages.Companion.USERNAME_MUST_MATCH_PATTERN
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Entity
@JsonRootName("user")
@RegisterForReflection
data class User(
    /**
     * Hibernate pattern validator.
     * See: https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-gettingstarted-createmodel
     */
    @field:Id
    @field:Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = USERNAME_MUST_MATCH_PATTERN)
    @field:JsonProperty("username")
    var username: String = "",

    @field:Email
    @field:NotBlank(message = EMAIL_MUST_BE_NOT_BLANK)
    @field:Column(unique = true)
    @field:JsonProperty("email")
    var email: String = "",

    @field:Transient
    @field:JsonProperty("token")
    var token: String = "",

    @field:NotBlank(message = PASSWORD_MUST_BE_NOT_BLANK)
    @field:JsonIgnore
    var password: String = "",

    @field:JsonProperty("bio")
    var bio: String = "",

    @field:JsonProperty("image")
    var image: String = "",

    @field:ManyToMany
    @field:JsonIgnore
    var follows: MutableList<User> = mutableListOf(),
) {
    override fun toString(): String = "User($username, $email, ${bio.take(20)}..., $image)"
}
