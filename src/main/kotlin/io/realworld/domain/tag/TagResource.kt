package io.realworld.domain.tag

import io.realworld.utils.ValidationMessages.Companion.REQUEST_BODY_MUST_NOT_BE_NULL
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.CREATED
import javax.ws.rs.core.Response.Status.OK
import javax.ws.rs.core.Response.created
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.UriBuilder.fromResource

@Path("/tags")
class TagResource {
    @Inject
    @field: Default
    lateinit var repository: TagRepository

    @GET
    @Produces(APPLICATION_JSON)
    fun list(): Response = repository.listAll().run {
        ok(TagsResponse.build(this)).status(OK).build()
    }

    @POST
    @Transactional
    @Consumes(APPLICATION_JSON)
    fun create(
        @Valid
        @NotNull(message = REQUEST_BODY_MUST_NOT_BE_NULL)
        newTag: Tag,
    ): Response = repository.persist(newTag).run {
        created(fromResource(TagResource::class.java).path("/" + newTag.name).build())
            .status(CREATED).build()
    }
}
