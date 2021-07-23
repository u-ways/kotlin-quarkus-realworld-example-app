package io.realworld.utils

import io.realworld.utils.QueryBuilder.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class QueryBuilderTest {
    @Test
    fun `Given a query with conditions only, when built, then IllegalStateException should be thrown`() {
        assertThrows<IllegalStateException> {
            QueryBuilder()
                .add(
                    WHERE("Home.address not member of person.addresses"),
                    AND("person.phones is not empty"),
                    AND("payment.completed = true")
                )
                .build()
        }
    }

    @Test
    fun `Given a valid query, when built, then correct query should be returned`() {
        val expectedCorrectQuery = """            
            SELECT articles from ArticleEntity as articles 
                INNER JOIN articles.tags as tags 
                INNER JOIN tags.primaryKey.tag as tag 
                INNER JOIN articles.author as authors 
                INNER JOIN articles.favorites as favorites 
                INNER JOIN favorites.primaryKey.user as user 
            WHERE upper(tag.name) in (:tags) 
                AND upper(user.username) in (:favorites) 
                AND upper(authors.username) in (:authors) 
        """

        val actual = QueryBuilder()
            .add(
                SELECT("articles from ArticleEntity as articles"),
                JOIN("articles.tags as tags"),
                JOIN("tags.primaryKey.tag as tag"),
                JOIN("articles.author as authors"),
                JOIN("articles.favorites as favorites"),
                JOIN("favorites.primaryKey.user as user"),
                WHERE("upper(tag.name) in (:tags)"),
                AND("upper(user.username) in (:favorites)"),
                AND("upper(authors.username) in (:authors)")
            )
            .build()

        assertEquals(expectedCorrectQuery.trim(), actual.trim())
    }

    @Test
    fun `Given a query with a false condition, when built, then false condition clause should not be added`() {
        val expected = SELECT("articles from ArticleEntity as articles")

        val tags = listOf<String>()
        val actual = QueryBuilder()
            .add(expected)
            .addIf(
                tags.isNotEmpty(),
                JOIN("articles.tags as tags")
            )
            .build()

        assertEquals(expected.build(), actual)
    }

    @Test
    fun `Given a query with a true condition with a runnable, when built, then condition runnable should be executed`() {
        val expected = "SELECT articles from ArticleEntity as articles INNER JOIN articles.tags as tags"

        val tags = listOf("cooking", "it")
        val parameters = hashMapOf<String, Any>()

        val actual = QueryBuilder()
            .add(SELECT("articles from ArticleEntity as articles"))
            .addIf(tags.isNotEmpty(), JOIN("articles.tags as tags")) { parameters["tags"] = tags }
            .build()

        assertEquals(expected, actual)
        assertEquals(tags, parameters["tags"])
    }

    @Test
    fun `Given a query with a false condition with a runnable, when built, then false condition runnable should not be executed`() {
        val expected = "SELECT articles from ArticleEntity as articles"

        val tags = listOf("cooking", "it")
        val parameters = hashMapOf<String, Any>()

        val actual = QueryBuilder()
            .add(SELECT("articles from ArticleEntity as articles"))
            .addIf(tags.isEmpty(), JOIN("articles.tags as tags")) { parameters["tags"] = tags }
            .build()

        assertEquals(expected, actual)
        assertNull(parameters["tags"])
    }
}

private fun String.trim(): String = this.replace(Regex("\\n|\\s"), "")
