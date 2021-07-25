package io.realworld.domain.article

import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.tag.Tag
import io.realworld.domain.user.User
import io.realworld.infrastructure.database.Tables.ARTICLE_TABLE
import io.realworld.infrastructure.database.Tables.TAG_RELATIONSHIP
import io.realworld.utils.ValidationMessages.Companion.TITLE_MUST_NOT_BE_BLANK
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import java.time.Instant.now
import java.util.*
import java.util.UUID.randomUUID
import javax.persistence.*
import javax.persistence.CascadeType.PERSIST
import javax.persistence.FetchType.EAGER
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Size

@Entity(name = ARTICLE_TABLE)
@RegisterForReflection
data class Article(
    @field:Id
    @field:Column(columnDefinition = "uuid")
    @field:GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var slug: UUID = randomUUID(),

    @field:Size(min = 5, max = 127)
    @field:NotBlank(message = TITLE_MUST_NOT_BE_BLANK)
    var title: String = "",

    @field:Size(min = 0, max = 255)
    var description: String = "",

    @field:Size(min = 0, max = 4095)
    var body: String = "",

    @field:Size(min = 0, max = 5)
    @field:ManyToMany(fetch = EAGER, cascade = [PERSIST])
    @field:JoinTable(name = TAG_RELATIONSHIP)
    var tagList: MutableList<Tag> = mutableListOf(),

    @field:PastOrPresent
    var createdAt: Instant = now(),

    @field:PastOrPresent
    var updatedAt: Instant = now(),

    @field:ManyToOne
    var author: User = User(),
) {
    override fun toString(): String =
        "Article($slug, $title, ${description.take(20)}, ${body.take(20)}, $createdAt, $updatedAt, ${author.username})"

    override fun hashCode(): Int = slug.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Article
        if (slug != other.slug) return false
        return true
    }
}
