package io.realworld.domain.comment

import io.realworld.domain.profile.FollowRelationshipRepository
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CommentService(
    private val repository: CommentRepository,
    private var followRelationshipRepository: FollowRelationshipRepository
) {
    fun list(slug: UUID, loggedInUserId: String) = CommentsResponse.build(
        repository.findByArticle(slug).map {
            CommentResponse.build(
                comment = it,
                isFollowing = followRelationshipRepository.isFollowing(
                    subjectedUserId = it.author.username,
                    loggedInUserId = loggedInUserId
                )
            )
        }
    )

    fun create(newCommentRequest: CommentCreateRequest, slug: UUID, loggedInUserId: String) = newCommentRequest
        .toComment(slug, loggedInUserId)
        .run {
            repository.persistAndFlush(this)
            CommentResponse.build(
                comment = this,
                isFollowing = followRelationshipRepository.isFollowing(
                    subjectedUserId = this.author.username,
                    loggedInUserId = loggedInUserId
                )
            )
        }

    fun delete(id: Long): Boolean = repository.deleteById(id)

    fun isCommentAuthor(id: Long, username: String?): Boolean =
        if (username == null) false else repository.exists(id, username)
}
