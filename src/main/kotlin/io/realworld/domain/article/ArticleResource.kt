package io.realworld.domain.article

import com.fasterxml.jackson.databind.ObjectMapper
import io.realworld.domain.exception.InvalidAuthorException
import io.realworld.infrastructure.security.Role.ADMIN
import io.realworld.infrastructure.security.Role.USER
import io.realworld.infrastructure.web.NoJsonRootWrap
import io.realworld.infrastructure.web.Routes.ARTICLES_PATH
import io.realworld.utils.ValidationMessages.Companion.REQUEST_BODY_MUST_NOT_BE_NULL
import java.util.*
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.CREATED
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.core.UriBuilder

@Path(ARTICLES_PATH)
class ArticleResource(
    @NoJsonRootWrap
    private val objectMapper: ObjectMapper, // FIXME: Using this annotation is throwing an unrecommended warning, see log.
    private val service: ArticleService,
) {
    @GET
    @Produces(APPLICATION_JSON)
    @PermitAll
    fun list(
        @QueryParam("limit") @DefaultValue("20") limit: Int = 20,
        @QueryParam("offset") @DefaultValue("0") offset: Int = 0,
        @QueryParam("tag") tags: List<String> = listOf(),
        @QueryParam("author") authors: List<String> = listOf(),
        @QueryParam("favorited") favorites: List<String> = listOf(),
        @Context securityContext: SecurityContext?
    ): Response = ok(
        objectMapper.writeValueAsString(
            service.list(limit, offset, tags, authors, favorites, securityContext?.userPrincipal?.name)
        )
    ).build()

    @GET
    @Path("/feed")
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER)
    fun feed(
        @QueryParam("limit") @DefaultValue("20") limit: Int = 20,
        @QueryParam("offset") @DefaultValue("0") offset: Int = 0,
        @Context securityContext: SecurityContext
    ): Response = ok(
        service.feed(limit, offset, securityContext.userPrincipal.name)
    ).build()

    @GET
    @Path("/{slug}")
    @Produces(APPLICATION_JSON)
    @PermitAll
    fun get(
        @PathParam("slug") slug: UUID,
        @Context securityContext: SecurityContext
    ): Response = ok(
        service.get(slug, securityContext.userPrincipal.name)
    ).build()

    @POST
    @Transactional
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER, ADMIN)
    fun create(
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) newArticle: ArticleCreateRequest,
        @Context securityContext: SecurityContext,
    ): Response = service.create(newArticle, securityContext.userPrincipal.name).run {
        ok(this)
            .status(CREATED)
            .location(UriBuilder.fromResource(ArticleResource::class.java).path("/$slug").build())
            .build()
    }

    @PUT
    @Path("/{slug}")
    @Transactional
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER, ADMIN)
    fun update(
        @PathParam("slug") slug: UUID,
        @Valid @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL) updateRequest: ArticleUpdateRequest,
        @Context securityContext: SecurityContext
    ): Response = securityContext.run {
        if (service.isArticleAuthor(slug, userPrincipal.name) || isUserInRole(ADMIN)) {
            ok(service.update(slug, updateRequest)).build()
        } else throw InvalidAuthorException()
    }

    @DELETE
    @Path("/{slug}")
    @Transactional
    @Produces(APPLICATION_JSON)
    @RolesAllowed(USER, ADMIN)
    fun delete(
        @PathParam("slug") slug: UUID,
        @Context securityContext: SecurityContext
    ): Response = securityContext.run {
        if (service.isArticleAuthor(slug, userPrincipal.name) || isUserInRole(ADMIN)) {
            ok(service.delete(slug)).build()
        } else throw InvalidAuthorException()
    }
}
