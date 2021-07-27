package io.realworld.domain.profile

import io.realworld.infrastructure.security.Role.USER
import io.realworld.infrastructure.web.Routes.PROFILES_PATH
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.OK
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.SecurityContext

@Path(PROFILES_PATH)
class ProfileResource {
    @Inject
    @Default
    lateinit var service: ProfileService

    @GET
    @Path("/{username}")
    @Produces(APPLICATION_JSON)
    @PermitAll
    fun getProfile(
        @Context securityContext: SecurityContext,
        @PathParam("username") username: String
    ): Response = ok(service.findProfile(username, securityContext.userPrincipal.name)).status(OK).build()

    @POST
    @Path("/{username}/follow")
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER)
    fun follow(
        @Context securityContext: SecurityContext,
        @PathParam("username") username: String
    ): Response = service.follow(username, securityContext.userPrincipal.name).run {
        ok(service.findProfile(username, securityContext.userPrincipal.name)).status(OK).build()
    }

    @DELETE
    @Path("/{username}/follow")
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER)
    fun unfollow(
        @Context securityContext: SecurityContext,
        @PathParam("username") username: String
    ): Response = service.unfollow(username, securityContext.userPrincipal.name).run {
        ok(service.findProfile(username, securityContext.userPrincipal.name)).status(OK).build()
    }
}
