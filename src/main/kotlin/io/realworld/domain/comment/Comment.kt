package io.realworld.domain.comment

import io.quarkus.runtime.annotations.RegisterForReflection
import io.realworld.domain.article.Article
import io.realworld.domain.user.User
import io.realworld.infrastructure.database.Tables.COMMENT_TABLE
import java.time.Instant
import java.time.Instant.now
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Size

@Entity(name = COMMENT_TABLE)
@RegisterForReflection
open class Comment(
    @Id
    @GeneratedValue
    open var id: Long = 0,

    @field:Size(min = 0, max = 1023)
    open var body: String = "",

    @field:PastOrPresent
    open var createdAt: Instant = now(),

    @field:PastOrPresent
    open var updatedAt: Instant = now(),

    @ManyToOne
    open var author: User = User(),

    @ManyToOne(fetch = LAZY)
    open var article: Article = Article(),
) {
    override fun toString(): String = "Comment($id, ${body.take(20)}, $createdAt, $updatedAt, ${author.username})"

    final override fun hashCode(): Int = id.hashCode()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Comment) return false
        if (id != other.id) return false
        return true
    }
}
