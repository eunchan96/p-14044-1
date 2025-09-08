package com.back.domain.post.postUser.service

import com.back.domain.post.postUser.repository.PostUserRepository
import com.back.standard.extensions.getOrThrow
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostUserAttrServiceTest {
    @Autowired
    private lateinit var postUserAttrService: PostUserAttrService

    @Autowired
    private lateinit var postUserRepository: PostUserRepository

    @Test
    @DisplayName("postCount 증가")
    fun t1() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        postUserAttrService.incrementPostsCount(postUser1)
    }

    @Test
    @DisplayName("postCount 감소")
    fun t2() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        postUserAttrService.decrementPostsCount(postUser1)
    }

    @Test
    @DisplayName("postCommentsCount 증가")
    fun t3() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        postUserAttrService.incrementPostCommentsCount(postUser1)
    }

    @Test
    @DisplayName("postCommentsCount 감소")
    fun t4() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        postUserAttrService.decrementPostCommentsCount(postUser1)
    }

    @Test
    @DisplayName("postCount 조회")
    fun t5() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        postUserAttrService.getPostsCount(postUser1)
    }

    @Test
    @DisplayName("postCommentsCount 조회")
    fun t6() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        postUserAttrService.getPostCommentsCount(postUser1)
    }
}