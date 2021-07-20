package io.realworld.support.factory

import io.realworld.domain.profile.ProfileResponse
import io.realworld.domain.user.User
import java.util.UUID.randomUUID

class ProfileFactory {
    companion object {
        /**
         * Creates a profile based on a given user.
         */
        fun create(
            user: User,
            following: Boolean = false,
        ): ProfileResponse = ProfileResponse(user.username, user.bio, user.image, following)

        /**
         * Creates a random profile.
         */
        fun create(
            username: String = "User-${randomUUID()}".substring(0, 10),
            bio: String = "Hello, I am $username!",
            image: String = "path/to/$username.jpg",
            following: Boolean = false,
        ): ProfileResponse = ProfileResponse(username, bio, image, following)

        /**
         * Creates X amount of profiles with random details
         * The set of can be considered to follow a mutual user if following is set to true.
         */
        fun create(
            amount: Int,
            following: Boolean = false,
        ): List<ProfileResponse> = (0 until amount).map { create(following = following) }
    }
}
