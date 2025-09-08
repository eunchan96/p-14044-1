package com.back.domain.post.postUser.service

import com.back.domain.post.postUser.entity.PostUser
import com.back.domain.post.postUser.entity.PostUserAttr
import com.back.domain.post.postUser.repository.PostUserAttrRepository
import org.springframework.stereotype.Service

@Service
class PostUserAttrService(
    private val postUserAttrRepository: PostUserAttrRepository
) {
    fun findBySubjectAndName(subject: PostUser, name: String): PostUserAttr? = postUserAttrRepository.findBySubjectAndName(subject, name)

    fun incrementPostsCount(subject: PostUser) {
        val attr = findBySubjectAndName(subject, "postsCount")
        if (attr == null) {
            postUserAttrRepository.save(PostUserAttr(subject, "postsCount", "1"))
        } else {
            val currentCount = attr.value.toIntOrNull() ?: 0
            attr.value = (currentCount + 1).toString()
            postUserAttrRepository.save(attr)
        }
    }

    fun decrementPostsCount(subject: PostUser) {
        val attr = findBySubjectAndName(subject, "postsCount")
        if (attr == null) {
            postUserAttrRepository.save(PostUserAttr(subject, "postsCount", "-1"))
        } else {
            val currentCount = attr.value.toIntOrNull() ?: 0
            attr.value = (currentCount - 1).toString()
            postUserAttrRepository.save(attr)
        }
    }

    fun incrementPostCommentsCount(subject: PostUser) {
        val attr = findBySubjectAndName(subject, "postCommentsCount")
        if (attr == null) {
            postUserAttrRepository.save(PostUserAttr(subject, "postCommentsCount", "1"))
        } else {
            val currentCount = attr.value.toIntOrNull() ?: 0
            attr.value = (currentCount + 1).toString()
            postUserAttrRepository.save(attr)
        }
    }

    fun decrementPostCommentsCount(subject: PostUser) {
        val attr = findBySubjectAndName(subject, "postCommentsCount")
        if (attr == null) {
            postUserAttrRepository.save(PostUserAttr(subject, "postCommentsCount", "-1"))
        } else {
            val currentCount = attr.value.toIntOrNull() ?: 0
            attr.value = (currentCount - 1).toString()
            postUserAttrRepository.save(attr)
        }
    }

    fun getPostsCount(subject: PostUser): Int {
        val attr = findBySubjectAndName(subject, "postsCount")
        val count = attr?.value?.toIntOrNull() ?: 0
        return count
    }

    fun getPostCommentsCount(subject: PostUser): Int {
        val attr = findBySubjectAndName(subject, "postCommentsCount")
        val count = attr?.value?.toIntOrNull() ?: 0
        return count
    }
}