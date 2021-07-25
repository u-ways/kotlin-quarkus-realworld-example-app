package io.realworld.domain.user

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.quarkus.test.security.TestSecurity
import io.realworld.infrastructure.security.BCryptHashProvider
import io.realworld.infrastructure.security.Role.USER
import io.realworld.support.factory.UserFactory
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import javax.inject.Inject
import javax.ws.rs.core.HttpHeaders.LOCATION
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response.Status.*

@QuarkusTest
@TestHTTPEndpoint(UserResource::class)
internal class UserResourceIT {
    @InjectMock
    lateinit var repository: UserRepository

    @Inject
    lateinit var hashProvider: BCryptHashProvider

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Given a new user, when a registration request is made, then response should be created`() {
        val newUser = UserFactory.create()
        val userRegistrationReq = newUser.run {
            UserRegistrationRequest(username, email, password)
        }

        `when`(repository.register(userRegistrationReq))
            .thenReturn(newUser)

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(userRegistrationReq))
            .`when`()
            .post("/users")
            .then()
            .header(LOCATION, containsString("/users/${newUser.username}"))
            .statusCode(CREATED.statusCode)

        verify(repository).register(userRegistrationReq)
    }

    @Test
    fun `Given a invalid registration request, when a request is made, then response should be bad request`() {
        val invalidEntity = UserFactory.create(username = "$^%@&", email = "@@@@")

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(invalidEntity)
            .`when`()
            .post("/users")
            .then()
            .statusCode(BAD_REQUEST.statusCode)

        verify(repository, never()).persist(invalidEntity)
    }

    @Test
    fun `Given a valid login details, when a login request is made, then response should be ok with correct user payload`() {
        val requestedUser = UserFactory.create()
        val userLoginReq = UserLoginRequest(requestedUser.email, requestedUser.password)

        `when`(repository.findByEmail(requestedUser.email))
            .thenReturn(requestedUser.copy(password = hashProvider.hash(requestedUser.password)))

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(userLoginReq))
            .`when`()
            .post("/users/login")
            .then()
            .body("size()", equalTo(1))
            .body("user.size()", equalTo(5))
            .body("user.username", equalTo(requestedUser.username))
            .body("user.email", equalTo(requestedUser.email))
            .body("user.password", nullValue())
            .body("user.token", notNullValue())
            .body("user.bio", equalTo(requestedUser.bio))
            .body("user.image", equalTo(requestedUser.image))
            .contentType(APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(repository).findByEmail(requestedUser.email)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given an already logged in user, when a get users request is made, then response should return current logged in user details`() {
        val loggedInUser = UserFactory.create(username = "loggedInUser")

        `when`(repository.findById(loggedInUser.username))
            .thenReturn(loggedInUser)

        given()
            .accept(APPLICATION_JSON)
            .get("/user")
            .then()
            .body("size()", equalTo(1))
            .body("user.size()", equalTo(5))
            .body("user.username", equalTo(loggedInUser.username))
            .body("user.email", equalTo(loggedInUser.email))
            .body("user.password", nullValue())
            .body("user.token", notNullValue())
            .body("user.bio", equalTo(loggedInUser.bio))
            .body("user.image", equalTo(loggedInUser.image))
            .contentType(APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(repository).findById(loggedInUser.username)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given logged in user, when a valid update request is made, then response should return updated user`() {
        val loggedInUser = UserFactory.create(username = "loggedInUser")
        val userUpdateReq = loggedInUser.run {
            UserUpdateRequest("newUsername", null, "newPassword", "newBio", "")
        }

        `when`(repository.update(loggedInUser.username, userUpdateReq))
            .thenReturn(
                loggedInUser.copy(
                    username = userUpdateReq.username!!,
                    password = userUpdateReq.password!!,
                    bio = userUpdateReq.bio!!,
                    image = userUpdateReq.image!!
                )
            )

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(userUpdateReq))
            .`when`()
            .put("/user")
            .then()
            .body("size()", equalTo(1))
            .body("user.size()", equalTo(5))
            .body("user.username", equalTo(userUpdateReq.username))
            .body("user.email", equalTo(loggedInUser.email))
            .body("user.password", nullValue())
            .body("user.token", notNullValue())
            .body("user.bio", equalTo(userUpdateReq.bio))
            .body("user.image", equalTo(userUpdateReq.image))
            .contentType(APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(repository).update(loggedInUser.username, userUpdateReq)
    }
}
