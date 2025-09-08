package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUserAttr
import com.back.standard.extensions.getOrThrow
import org.assertj.core.api.Assertions.assertThat
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
class PostUserAttrRepositoryTest {
    @Autowired
    private lateinit var postUserAttrRepository: PostUserAttrRepository

    @Autowired
    private lateinit var postUserRepository: PostUserRepository

    @Test
    @DisplayName("saveInt")
    fun t1() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        val attr = PostUserAttr(postUser1, "postCount", 0.toString())
        postUserAttrRepository.save(attr)

        val result = postUserAttrRepository.findBySubjectAndName(postUser1, "postCount")
        assertThat(result).isNotNull
    }

    @Test
    @DisplayName("saveString")
    fun t2() {
        val postUser1 = postUserRepository.findByUsername("user1").getOrThrow()
        val attr = PostUserAttr(postUser1, "grade", "user")
        postUserAttrRepository.save(attr)

        val result = postUserAttrRepository.findBySubjectAndName(postUser1, "grade")
        assertThat(result).isNotNull
    }
}