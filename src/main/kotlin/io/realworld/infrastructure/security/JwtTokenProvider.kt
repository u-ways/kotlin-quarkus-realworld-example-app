package io.realworld.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWT.require
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.algorithms.Algorithm.HMAC512
import com.auth0.jwt.interfaces.DecodedJWT
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.Instant.now
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * Json Web Token
 * See: https://datatracker.ietf.org/doc/html/rfc7519
 * See: https://auth0.com/learn/json-web-tokens/
 * See: https://github.com/auth0/java-jwt
 */
@ApplicationScoped
class JwtTokenProvider(
    @ConfigProperty(name = "jwt.issuer")
    private val issuer: String,
    @ConfigProperty(name = "jwt.secret")
    private val secret: String,
    @ConfigProperty(name = "jwt.expiration.time.minutes")
    private val expirationTimeInMinutes: Long,
    private val algorithm: Algorithm = HMAC512(secret),
    private val jwtVerifier: JWTVerifier = require(algorithm).withIssuer(issuer).build()
) {
    fun create(userId: String): String = JWT.create()
        .withIssuer(issuer)
        .withSubject(userId)
        .withIssuedAt(Date())
        .withExpiresAt(
            Date(
                now().plus(expirationTimeInMinutes, MINUTES).toEpochMilli()
            )
        )
        .sign(algorithm)

    fun verify(token: String): DecodedJWT = jwtVerifier.verify(token)
}
