package io.realworld.domain.comment

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.realworld.domain.profile.FollowRelationshipRepository
import io.realworld.support.factory.CommentFactory
import io.realworld.support.factory.UserFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.util.UUID.randomUUID

@QuarkusTest
internal class CommentServiceTest {
    @InjectMock
    lateinit var commentRepository: CommentRepository
    @InjectMock
    lateinit var followRelationshipRepository: FollowRelationshipRepository

    private lateinit var service: CommentService

    @BeforeEach
    internal fun setUp() {
        service = CommentService(
            commentRepository,
            followRelationshipRepository
        )
    }

    @Test
    fun `Given a valid article's slug with related comments, when list is requested, then service should return correct comments response`() {
        val slug = randomUUID()
        val loggedInUser = UserFactory.create()
        val relatedComments = CommentFactory.create(3)

        `when`(commentRepository.findByArticle(slug))
            .thenReturn(relatedComments)

        `when`(followRelationshipRepository.isFollowing(any(), any()))
            .thenReturn(false)

        val expectedCommentsResponse = CommentsResponse.build(
            relatedComments.map {
                CommentResponse.build(
                    comment = it,
                    isFollowing = false
                )
            }
        )

        assertEquals(service.list(slug, loggedInUser.username), expectedCommentsResponse)

        verify(commentRepository).findByArticle(slug)
        verify(followRelationshipRepository, times(3)).isFollowing(any(), any())
    }

    @Test
    fun `Given a new comment, when a create is requested, service should persist new comment`() {
        val slug = randomUUID()
        val loggedInUser = UserFactory.create()

        `when`(followRelationshipRepository.isFollowing(any(), any()))
            .thenReturn(false)

        service.create(CommentCreateRequest(""), slug, loggedInUser.username)

        verify(commentRepository).persistAndFlush(any())
        verify(followRelationshipRepository).isFollowing(any(), any())
    }

    @Test
    fun `Given an existing comment, when delete is requested, then service should delete by id`() {
        val id: Long = 10

        `when`(commentRepository.deleteById(id)).thenReturn(true)

        service.delete(id)

        verify(commentRepository).deleteById(id)
    }
}
