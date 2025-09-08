package com.back.domain.post.post.repository

import com.back.standard.search.MemberSearchSortType
import com.back.standard.search.PostSearchKeywordType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostRepositoryTest {
    @Autowired
    private lateinit var postRepository: PostRepository

    @Test
    @DisplayName("findByKeyword")
    fun t1() {
        val pageable = PageRequest.of(0, 10)
        val result = postRepository.findByKeyword(PostSearchKeywordType.TITLE,"발야구", pageable)

        assertThat(result).allMatch { it.title.contains("발야구") }
    }

    @Test
    @DisplayName("findByKeyword - sortByAsc")
    fun t2() {
        val pageable = PageRequest.of(0, 10, MemberSearchSortType.USERNAME_ASC.sortBy)
        val result = postRepository.findByKeyword(PostSearchKeywordType.TITLE,"발야구", pageable).content

        for (i in 0 until result.size - 1) {
            assertThat(result[i].title).isLessThan(result[i + 1].title)
        }
    }

    @Test
    @DisplayName("findByKeyword - searchByAuthor")
    fun t3() {
        val pageable = PageRequest.of(0, 10)
        val result = postRepository.findByKeyword(PostSearchKeywordType.AUTHOR_NAME,"유저", pageable).content

        assertThat(result).allMatch { it.author.name.contains("유저") }
        assertThat(result).noneMatch { it.author.name.contains("시스템") || it.author.name.contains("관리자") }
    }
}