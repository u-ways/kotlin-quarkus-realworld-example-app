package io.realworld.domain.user

import io.realworld.domain.exception.EmailAlreadyExistsException
import io.realworld.domain.exception.InvalidPasswordException
import io.realworld.domain.exception.UnregisteredEmailException
import io.realworld.domain.exception.UserNotFoundException
import io.realworld.domain.exception.UsernameAlreadyExistsException
import io.realworld.infrastructure.security.BCryptHashProvider
import io.realworld.infrastructure.security.JwtTokenProvider
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserService(
    private val repository: UserRepository,
    private val tokenProvider: JwtTokenProvider,
    private val hashProvider: BCryptHashProvider,
) {
    fun get(username: String): UserResponse = repository.findById(username)?.run {
        UserResponse.build(this, tokenProvider.create(username))
    } ?: throw UserNotFoundException()

    fun register(newUser: UserRegistrationRequest): UserResponse = newUser.run {
        if (repository.existsUsername(newUser.username)) throw UsernameAlreadyExistsException()
        if (repository.existsEmail(newUser.email)) throw EmailAlreadyExistsException()

        UserResponse.build(
            this.toEntity().also {
                it.password = hashProvider.hash(password)
                repository.persist(it)
            },
            tokenProvider.create(username)
        )
    }

    fun login(userLoginRequest: UserLoginRequest) = repository.findByEmail(userLoginRequest.email)?.run {
        if (!hashProvider.verify(userLoginRequest.password, password)) throw InvalidPasswordException()
        else UserResponse.build(this, tokenProvider.create(username))
    } ?: throw UnregisteredEmailException()

    fun update(loggedInUserId: String, updateRequest: UserUpdateRequest): UserResponse = repository
        .findById(loggedInUserId)
        ?.run {
            if (updateRequest.username != null &&
                updateRequest.username != username &&
                repository.existsUsername(updateRequest.username)
            ) throw UsernameAlreadyExistsException()

            if (updateRequest.email != null &&
                updateRequest.email != email &&
                repository.existsEmail(updateRequest.email)
            ) throw EmailAlreadyExistsException()

            UserResponse.build(
                updateRequest.applyChangesTo(this).apply {
                    if (updateRequest.password != null) this.password = hashProvider.hash(password)
                    repository.persist(this)
                },
                tokenProvider.create(username)
            )
        } ?: throw UserNotFoundException()
}
