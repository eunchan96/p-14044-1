package com.back.domain.post.post.service

import com.back.domain.post.post.dto.PostDto
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.repository.PostRepository
import com.back.domain.post.postComment.dto.PostCommentDto
import com.back.domain.post.postComment.entity.PostComment
import com.back.domain.post.postComment.event.PostCommentWrittenEvent
import com.back.domain.post.postUser.dto.PostUserDto
import com.back.domain.post.postUser.entity.PostUser
import com.back.standard.search.PostSearchKeywordType
import com.back.standard.search.PostSearchKeywordType.TITLE
import com.back.standard.search.PostSearchSortType
import com.back.standard.search.PostSearchSortType.ID
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val publisher: ApplicationEventPublisher
) {
    fun count(): Long {
        return postRepository.count()
    }

    fun write(author: PostUser, title: String, content: String): Post {
        val post = Post(author, title, content)

        author.incrementPostsCount()

        return postRepository.save(post)
    }

    fun findById(id: Int): Post? = postRepository.findById(id).orElse(null)

    fun findAll(): List<Post> = postRepository.findAll()

    fun modify(post: Post, title: String, content: String) = post.modify(title, content)

    fun writeComment(author: PostUser, post: Post, content: String): PostComment {
        val postComment = post.addComment(author, content)
        flush()

        publisher.publishEvent(
            PostCommentWrittenEvent(
                PostUserDto(author),
                PostUserDto(post.author),
                PostDto(post),
                PostCommentDto(postComment)
            )
        )

        return postComment
    }

    fun deleteComment(post: Post, postComment: PostComment): Boolean = post.deleteComment(postComment)

    fun modifyComment(postComment: PostComment, content: String) = postComment.modify(content)

    fun delete(post: Post) {
        post.author.decrementPostsCount()
//        post.comments.forEach { it.author.decrementPostCommentsCount() }

        postRepository.delete(post)
    }

    fun findLatest(): Post? = postRepository.findFirstByOrderByIdDesc()

    fun flush() = postRepository.flush()

    fun findBySearchPaged(
        keywordType: PostSearchKeywordType = TITLE,
        keyword: String = "",
        page: Int = 1,
        pageSize: Int = 30,
        sort: PostSearchSortType = ID
    ): Page<Post> {
        val pageSize = if (pageSize in 1..100) pageSize else 30
        val page = if (page > 0) page else 1
        val pageable = PageRequest.of(page - 1, pageSize, sort.sortBy)
        return postRepository.findByKeyword(keywordType, keyword, pageable)
    }
}