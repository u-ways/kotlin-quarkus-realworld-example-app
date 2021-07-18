package io.realworld.support.factory

import io.realworld.domain.user.User
import java.util.UUID.randomUUID

class UserFactory {
    companion object {
        /**
         * Creates a random User.
         */
        fun create(
            username: String = "User-${randomUUID()}".substring(0, 10),
            email: String = "$username@email.com",
            token: String = "token-$username",
            password: String = "password",
            bio: String = "Hello, I am $username!",
            image: String = "path/to/$username.jpg",
            follows: MutableList<User> = mutableListOf(),
        ): User = User(username, email, token, password, bio, image, follows)

        /**
         * Creates X amount of User with random details
         * A set of mutual users to follow can be passed to the follows argument.
         */
        fun create(
            amount: Int,
            follows: MutableList<User> = mutableListOf(),
        ): List<User> = (0 until amount).map { create(follows = follows) }
    }
}
