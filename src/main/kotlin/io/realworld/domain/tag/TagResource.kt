package io.realworld.domain.tag

import io.realworld.infrastructure.web.Routes.TAGS_PATH
import io.realworld.utils.ValidationMessages.Companion.REQUEST_BODY_MUST_NOT_BE_NULL
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.CREATED
import javax.ws.rs.core.Response.Status.OK
import javax.ws.rs.core.Response.created
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.UriBuilder.fromResource

@Path(TAGS_PATH)
class TagResource(
    private val repository: TagRepository
) {

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
