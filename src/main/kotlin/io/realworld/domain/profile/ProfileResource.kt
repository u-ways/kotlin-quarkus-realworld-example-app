package io.realworld.domain.profile

import io.realworld.domain.exception.ProfileNotFoundException
import io.realworld.infrastructure.security.Role.USER
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

@Path("/profiles")
class ProfileResource {

    @Inject
    @field:Default
    lateinit var repository: ProfileRepository

    @GET
    @Path("/{username}")
    @Produces(APPLICATION_JSON)
    @PermitAll
    fun getProfile(
        @Context securityContext: SecurityContext,
        @PathParam("username") username: String
    ): Response = repository.findProfile(securityContext.userPrincipal.name, username)?.run {
        ok(this).status(OK).build()
    } ?: throw ProfileNotFoundException()

    @POST
    @Path("/{username}/follow")
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER)
    fun follow(
        @Context securityContext: SecurityContext,
        @PathParam("username") username: String
    ): Response = repository.follow(securityContext.userPrincipal.name, username).run {
        repository.findProfile(securityContext.userPrincipal.name, username)?.run {
            ok(this).status(OK).build()
        } ?: throw ProfileNotFoundException()
    }

    @DELETE
    @Path("/{username}/follow")
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER)
    fun unfollow(
        @Context securityContext: SecurityContext,
        @PathParam("username") username: String
    ): Response = repository.unfollow(securityContext.userPrincipal.name, username).run {
        repository.findProfile(securityContext.userPrincipal.name, username)?.run {
            ok(this).status(OK).build()
        } ?: throw ProfileNotFoundException()
    }
}
