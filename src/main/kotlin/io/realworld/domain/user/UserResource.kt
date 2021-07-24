package io.realworld.domain.user

import io.realworld.domain.exception.InvalidPasswordException
import io.realworld.domain.exception.UnregisteredEmailException
import io.realworld.domain.exception.UserNotFoundException
import io.realworld.infrastructure.security.BCryptHashProvider
import io.realworld.infrastructure.security.JwtTokenProvider
import io.realworld.infrastructure.security.Role.ADMIN
import io.realworld.infrastructure.security.Role.USER
import io.realworld.utils.ValidationMessages.Companion.REQUEST_BODY_MUST_NOT_BE_NULL
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.CREATED
import javax.ws.rs.core.Response.Status.OK
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.core.UriBuilder.fromResource

@Path("/")
class UserResource {
    @Inject
    @field:Default
    lateinit var repository: UserRepository

    @Inject
    @field:Default
    lateinit var hashProvider: BCryptHashProvider

    @Inject
    @field:Default
    lateinit var tokenProvider: JwtTokenProvider

    @POST
    @Path("/users")
    @Transactional
    @Consumes(APPLICATION_JSON)
    @PermitAll
    fun register(
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) newUser: UserRegistrationRequest,
    ): Response = repository.register(newUser).run {
        ok(UserResponse.build(this, tokenProvider.create(username)))
            .status(CREATED)
            .location(fromResource(UserResource::class.java).path("/users/$username").build())
            .build()
    }

    @POST
    @Path("/users/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @PermitAll
    fun login(
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) userLoginRequest: UserLoginRequest
    ): Response = repository.findByEmail(userLoginRequest.email)?.run {
        if (!hashProvider.verify(userLoginRequest.password, password)) throw InvalidPasswordException()
        else ok(UserResponse.build(this, tokenProvider.create(username))).status(OK).build()
    } ?: throw UnregisteredEmailException()

    @GET
    @Path("/user")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER)
    fun getLoggedInUser(
        @Context securityContext: SecurityContext
    ): Response = repository.findById(securityContext.userPrincipal.name)?.run {
        ok(UserResponse.build(this, tokenProvider.create(username))).status(OK).build()
    } ?: throw UserNotFoundException()

    @PUT
    @Path("/user")
    @Transactional
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER, ADMIN)
    fun updateLoggedInUser(
        @Context securityContext: SecurityContext,
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) userUpdateRequest: UserUpdateRequest,
    ): Response = repository.update(securityContext.userPrincipal.name, userUpdateRequest).run {
        ok(UserResponse.build(this, tokenProvider.create(username))).status(OK).build()
    }
}
