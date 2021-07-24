package io.realworld.domain.user

import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.infrastructure.database.Tables.FOLLOW_RELATIONSHIP
import io.realworld.infrastructure.database.Tables.USER_TABLE
import io.realworld.utils.Patterns.Companion.ALPHANUMERICAL
import io.realworld.utils.ValidationMessages.Companion.EMAIL_MUST_BE_NOT_BLANK
import io.realworld.utils.ValidationMessages.Companion.PASSWORD_MUST_BE_NOT_BLANK
import io.realworld.utils.ValidationMessages.Companion.USERNAME_MUST_MATCH_PATTERN
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Entity(name = USER_TABLE)
@RegisterForReflection
data class User(
    @field:Id
    @field:Pattern(regexp = ALPHANUMERICAL, message = USERNAME_MUST_MATCH_PATTERN)
    var username: String = "",

    @field:Email
    @field:NotBlank(message = EMAIL_MUST_BE_NOT_BLANK)
    @field:Column(unique = true)
    var email: String = "",

    @field:NotBlank(message = PASSWORD_MUST_BE_NOT_BLANK)
    var password: String = "",

    @field:Size(min = 0, max = 255)
    var bio: String = "",

    @field:Size(min = 0, max = 2097152) // max = 1920 x 1080-pixel resolution
    var image: String = "",

    @field:ManyToMany
    @field:JoinTable(
        name = FOLLOW_RELATIONSHIP,
        joinColumns = [JoinColumn(name = "userId", referencedColumnName = "username")],
        inverseJoinColumns = [JoinColumn(name = "followingId", referencedColumnName = "username")]
    )
    var follows: MutableList<User> = mutableListOf(),
) {
    override fun toString(): String = "User($username, $email, ${bio.take(20)}..., $image)"

    override fun hashCode(): Int = username.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        if (username != other.username) return false
        return true
    }
}
