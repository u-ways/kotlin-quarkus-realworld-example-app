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
open class Article(
    @Id
    @Column(columnDefinition = "uuid")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    open var slug: UUID = randomUUID(),

    @field:Size(min = 5, max = 127)
    @field:NotBlank(message = TITLE_MUST_NOT_BE_BLANK)
    open var title: String = "",

    @field:Size(min = 0, max = 255)
    open var description: String = "",

    @field:Size(min = 0, max = 4095)
    open var body: String = "",

    @field:Size(min = 0, max = 5)
    @ManyToMany(fetch = EAGER, cascade = [PERSIST])
    @JoinTable(name = TAG_RELATIONSHIP)
    open var tagList: MutableList<Tag> = mutableListOf(),

    @field:PastOrPresent
    open var createdAt: Instant = now(),

    @field:PastOrPresent
    open var updatedAt: Instant = now(),

    @ManyToOne
    open var author: User = User(),
) {
    override fun toString(): String =
        "Article($slug, $title, ${description.take(20)}, ${body.take(20)}, $createdAt, $updatedAt, ${author.username})"

    final override fun hashCode(): Int = slug.hashCode()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Article) return false
        if (slug != other.slug) return false
        return true
    }
}
