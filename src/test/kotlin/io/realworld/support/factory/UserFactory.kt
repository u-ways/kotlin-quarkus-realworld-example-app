package io.realworld.support.factory

import io.realworld.domain.user.User
import java.util.UUID.randomUUID

class UserFactory {
    companion object {
        /**
         * Creates a random User.
         */
        fun create(
            username: String = "User-${randomUUID()}".substring(0, 20),
            email: String = "$username@email.com",
            password: String = "password",
            bio: String = "Hello, I am $username!",
            image: String = "path/to/$username.jpg"
        ): User = User(username, email, password, bio, image)

        /**
         * Creates X amount of User with random details
         */
        fun create(
            amount: Int,
        ): List<User> = (0 until amount).map { create() }
    }
}
