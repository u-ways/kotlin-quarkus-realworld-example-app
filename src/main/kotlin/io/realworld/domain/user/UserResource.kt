package io.realworld.domain.user

import io.realworld.infrastructure.security.Role.ADMIN
import io.realworld.infrastructure.security.Role.USER
import io.realworld.infrastructure.web.Routes.USERS_PATH
import io.realworld.infrastructure.web.Routes.USER_PATH
import io.realworld.utils.ValidationMessages.Companion.REQUEST_BODY_MUST_NOT_BE_NULL
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
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
    lateinit var service: UserService

    @POST
    @Path(USERS_PATH)
    @Transactional
    @Consumes(APPLICATION_JSON)
    @PermitAll
    fun register(
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) newUser: UserRegistrationRequest,
    ): Response = service.register(newUser).run {
        ok(this).status(CREATED)
            .location(fromResource(UserResource::class.java).path("$USERS_PATH/$username").build())
            .build()
    }

    @POST
    @Path("$USERS_PATH/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @PermitAll
    fun login(
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) userLoginRequest: UserLoginRequest
    ): Response = ok(service.login(userLoginRequest)).status(OK).build()

    @GET
    @Path(USER_PATH)
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER)
    fun getLoggedInUser(
        @Context securityContext: SecurityContext
    ): Response = ok(service.get(securityContext.userPrincipal.name)).status(OK).build()

    @PUT
    @Path(USER_PATH)
    @Transactional
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER, ADMIN)
    fun updateLoggedInUser(
        @Context securityContext: SecurityContext,
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) userUpdateRequest: UserUpdateRequest,
    ): Response = ok(service.update(securityContext.userPrincipal.name, userUpdateRequest)).status(OK).build()
}
