package io.realworld.domain.comment

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.panache.common.Parameters
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CommentRepository : PanacheRepository<Comment> {
    fun findByArticle(slug: UUID): List<Comment> =
        find("article.slug", slug).list()

    fun exists(subjectedCommentId: Long, withAuthorId: String): Boolean = count(
        query = "id.id = :subjectedCommentId AND author.username = :withAuthorId",
        params = Parameters.with("subjectedCommentId", subjectedCommentId).and("withAuthorId", withAuthorId)
    ) > 0
}
