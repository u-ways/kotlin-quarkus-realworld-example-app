package io.realworld.domain.comment

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.quarkus.test.security.TestSecurity
import io.realworld.infrastructure.security.Role.ADMIN
import io.realworld.infrastructure.security.Role.USER
import io.realworld.infrastructure.web.Routes.ARTICLES_PATH
import io.realworld.support.factory.CommentFactory
import io.realworld.support.factory.UserFactory
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.HttpHeaders.LOCATION
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response.Status.CREATED
import javax.ws.rs.core.Response.Status.OK

@QuarkusTest
@TestHTTPEndpoint(CommentResource::class)
internal class CommentResourceIT {
    @InjectMock
    lateinit var service: CommentService

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given existing comments, when list is requested, then 200 response should be returned with correct payload`() {
        val slug = UUID.randomUUID()
        val loggedInUser = UserFactory.create(username = "loggedInUser")

        `when`(service.list(slug, loggedInUser.username))
            .thenReturn(
                CommentFactory.create(3).run {
                    CommentsResponse(map(CommentResponse::build))
                }
            )

        given()
            .accept(APPLICATION_JSON)
            .get("$slug/comments")
            .then()
            .body("size()", equalTo(1))
            .body("comments.size()", `is`(3))
            .contentType(APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(service).list(slug, loggedInUser.username)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given an authenticated user, when they create a new comment on an existing article, then created status should be returned`() {
        val slug = UUID.randomUUID()
        val loggedInUser = UserFactory.create(username = "loggedInUser")
        val commentResponse = CommentFactory.create(author = loggedInUser).run(CommentResponse::build)
        val newCommentRequest = commentResponse.run { CommentCreateRequest(body) }

        `when`(service.create(newCommentRequest, slug, loggedInUser.username))
            .thenReturn(commentResponse)

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(newCommentRequest))
            .post("$slug/comments")
            .then()
            .body("comment.size()", `is`(5))
            .body("comment.author.username", equalTo(loggedInUser.username))
            .contentType(APPLICATION_JSON)
            .header(LOCATION, containsString("$ARTICLES_PATH/$slug/comments/${commentResponse.id}"))
            .statusCode(CREATED.statusCode)

        verify(service).create(newCommentRequest, slug, loggedInUser.username)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given an authenticated user, when they delete their own comment, then an OK response should be returned`() {
        val slug = UUID.randomUUID()
        val loggedInUser = UserFactory.create(username = "loggedInUser")
        val commentToDelete = CommentFactory.create(author = loggedInUser)

        `when`(service.isCommentAuthor(commentToDelete.id, loggedInUser.username)).thenReturn(true)
        `when`(service.delete(commentToDelete.id)).thenReturn(true)

        given()
            .accept(APPLICATION_JSON)
            .`when`()
            .delete("$slug/comments/${commentToDelete.id}")
            .then()
            .statusCode(OK.statusCode)

        verify(service).delete(commentToDelete.id)
    }

    @Test
    @TestSecurity(user = "ADMIN", roles = [ADMIN])
    fun `Given an authenticated ADMIN, when they delete a user's comment, then an OK response should be returned`() {
        val slug = UUID.randomUUID()
        val authenticatedAdmin = UserFactory.create(username = "ADMIN")
        val commentToDelete = CommentFactory.create(author = UserFactory.create())

        `when`(service.isCommentAuthor(commentToDelete.id, authenticatedAdmin.username)).thenReturn(false)
        `when`(service.delete(commentToDelete.id)).thenReturn(true)

        given()
            .accept(APPLICATION_JSON)
            .`when`()
            .delete("$slug/comments/${commentToDelete.id}")
            .then()
            .statusCode(OK.statusCode)

        verify(service).delete(commentToDelete.id)
    }
}
