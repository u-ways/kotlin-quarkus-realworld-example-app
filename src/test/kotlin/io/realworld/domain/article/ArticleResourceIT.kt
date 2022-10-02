package io.realworld.domain.article

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.quarkus.test.security.TestSecurity
import io.realworld.domain.tag.Tag
import io.realworld.infrastructure.security.Role.USER
import io.realworld.infrastructure.web.Routes.ARTICLES_PATH
import io.realworld.support.factory.ArticleFactory
import io.realworld.support.factory.UserFactory
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.blankOrNullString
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.UUID.randomUUID
import javax.inject.Inject
import javax.ws.rs.core.HttpHeaders.LOCATION
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response.Status.CREATED
import javax.ws.rs.core.Response.Status.OK
import javax.ws.rs.core.Response.Status.UNAUTHORIZED

@QuarkusTest
@TestHTTPEndpoint(ArticleResource::class)
internal class ArticleResourceIT {
    @InjectMock
    lateinit var service: ArticleService
    @Inject
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Given existing articles, when a matching filtered list of articles are requested, then response should be 200 with correct payload`() {
        val existingArticles = ArticleFactory.create(
            amount = 3,
            tags = mutableListOf(Tag("UK"))
        )

        `when`(service.list(tags = listOf("UK"))).thenReturn(
            ArticlesResponse.build(existingArticles.map(ArticleResponse::build))
        )

        given()
            .accept(APPLICATION_JSON)
            .`when`()
            .get("?tag=UK")
            .then()
            .body("size()", equalTo(2))
            .body("articles.size()", equalTo(existingArticles.count()))
            .body("articlesCount", equalTo(existingArticles.count()))
            .contentType(APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(service).list(tags = listOf("UK"))
    }

    @Test
    fun `Given an unauthenticated user, when feed is requested, then unauthorized status should be returned`() {
        given()
            .accept(APPLICATION_JSON)
            .`when`()
            .get("feed")
            .then()
            .body(blankOrNullString())
            .statusCode(UNAUTHORIZED.statusCode)
    }

    @Test
    @TestSecurity(user = "anonymous", roles = [])
    fun `Given existing article, when a requested by slug, then response should be 200 with correct payload`() {
        val existingArticle = ArticleFactory.create()

        `when`(service.get(existingArticle.slug, "anonymous")).thenReturn(
            ArticleResponse.build(existingArticle)
        )

        given()
            .accept(APPLICATION_JSON)
            .`when`()
            .get("/${existingArticle.slug}")
            .then()
            .body("article.size()", equalTo(10))
            .body("article.slug", equalTo(existingArticle.slug.toString()))
            .body("article.title", equalTo(existingArticle.title))
            .body("article.description", equalTo(existingArticle.description))
            .body("article.body", equalTo(existingArticle.body))
            .body("article.tagList", equalTo(existingArticle.tagList))
            .body("article.createdAt", equalTo(existingArticle.createdAt.toString()))
            .body("article.updatedAt", equalTo(existingArticle.updatedAt.toString()))
            .body("article.favorited", equalTo(false))
            .body("article.favoritesCount", equalTo(0))
            .body("article.author.size()", equalTo(4))
            .body("article.author.username", equalTo(existingArticle.author.username))
            .body("article.author.image", equalTo(existingArticle.author.image))
            .body("article.author.bio", equalTo(existingArticle.author.bio))
            .body("article.author.following", equalTo(false))
            .contentType(APPLICATION_JSON)
            .statusCode(OK.statusCode)

        verify(service).get(existingArticle.slug, "anonymous")
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given an authenticated user, when a valid new article request is made, then response should be created`() {
        val loggedInUser = UserFactory.create(username = "loggedInUser")
        val newArticleRequest = ArticleFactory.create().run {
            ArticleCreateRequest(title, description, body)
        }
        val newArticle = newArticleRequest.toEntity(loggedInUser.username)

        `when`(service.create(newArticleRequest, loggedInUser.username)).thenReturn(
            ArticleResponse.build(newArticle)
        )

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(newArticleRequest))
            .`when`()
            .post()
            .then()
            .body("article.size()", equalTo(10))
            .body("article.slug", equalTo(newArticle.slug.toString()))
            .header(LOCATION, CoreMatchers.containsString("$ARTICLES_PATH/${newArticle.slug}"))
            .statusCode(CREATED.statusCode)

        verify(service).create(newArticleRequest, loggedInUser.username)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given an invalid article author, when an article update request is made, then unauthorized response should be returned`() {
        val slug = randomUUID()
        val loggedInUser = UserFactory.create(username = "loggedInUser")

        `when`(service.isArticleAuthor(slug, loggedInUser.username)).thenReturn(false)

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(ArticleUpdateRequest()))
            .`when`()
            .put("/$slug")
            .then()
            .body(blankOrNullString())
            .statusCode(UNAUTHORIZED.statusCode)

        verify(service).isArticleAuthor(slug, loggedInUser.username)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given an valid article author, when an article update request is made, then article should get updated`() {
        val slug = randomUUID()
        val loggedInUser = UserFactory.create(username = "loggedInUser")
        val articleUpdateRequest = ArticleUpdateRequest()

        `when`(service.isArticleAuthor(slug, loggedInUser.username))
            .thenReturn(true)

        `when`(service.update(slug, articleUpdateRequest)).thenReturn(
            ArticleResponse.build(ArticleFactory.create())
        )

        given()
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(articleUpdateRequest))
            .`when`()
            .put("/$slug")
            .then()
            .statusCode(OK.statusCode)

        verify(service).isArticleAuthor(slug, loggedInUser.username)
        verify(service).update(slug, articleUpdateRequest)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given an invalid article author, when an article delete request is made, then unauthorized response should be returned`() {
        val slug = randomUUID()
        val loggedInUser = UserFactory.create(username = "loggedInUser")

        `when`(service.isArticleAuthor(slug, loggedInUser.username)).thenReturn(false)

        given()
            .accept(APPLICATION_JSON)
            .`when`()
            .delete("/$slug")
            .then()
            .body(blankOrNullString())
            .statusCode(UNAUTHORIZED.statusCode)

        verify(service).isArticleAuthor(slug, loggedInUser.username)
    }

    @Test
    @TestSecurity(user = "loggedInUser", roles = [USER])
    fun `Given an valid article author, when an article delete request is made, then service should delete article`() {
        val slug = randomUUID()
        val loggedInUser = UserFactory.create(username = "loggedInUser")

        `when`(service.isArticleAuthor(slug, loggedInUser.username)).thenReturn(true)
        `when`(service.delete(slug)).thenReturn(true)

        given()
            .accept(APPLICATION_JSON)
            .`when`()
            .delete("/$slug")
            .then()
            .statusCode(OK.statusCode)

        verify(service).isArticleAuthor(slug, loggedInUser.username)
        verify(service).delete(slug)
    }
}
