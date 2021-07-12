package io.realworld.infrastructure.security

import at.favre.lib.crypto.bcrypt.BCrypt.*
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.ApplicationScoped

/**
 * OpenBSD Blowfish password hashing algorithm
 * See: https://github.com/patrickfav/bcrypt
 */
@ApplicationScoped
class BCryptHashProvider(
    @ConfigProperty(name = "bcrypt.hash.cost")
    private val hashCost: Int,
) {
    fun hash(password: String): String =
        withDefaults().hashToString(hashCost, password.toCharArray())

    fun verify(plaintext: String, hash: String): Result =
        verifyer().verify(plaintext.toCharArray(), hash)
}
