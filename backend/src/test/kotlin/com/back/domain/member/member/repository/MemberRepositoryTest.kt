package com.back.domain.member.member.repository

import com.back.standard.extensions.getOrThrow
import com.back.standard.search.MemberSearchKeywordType
import com.back.standard.search.MemberSearchSortType
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
class MemberRepositoryTest {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    @DisplayName("findByKeyword")
    fun t1() {
        val pageable = PageRequest.of(0, 10)
        val result = memberRepository.findByKeyword(MemberSearchKeywordType.USERNAME,"user", pageable)

        assertThat(result.all { it.username.contains("user") }).isTrue
    }

    @Test
    @DisplayName("findByKeyword - sortByAsc")
    fun t2() {
        val pageable = PageRequest.of(0, 10, MemberSearchSortType.USERNAME_ASC.sortBy)
        val result = memberRepository.findByKeyword(MemberSearchKeywordType.USERNAME,"user", pageable).content

        for (i in 0 until result.size - 1) {
            assertThat(result[i].username).isLessThan(result[i + 1].username)
        }
    }

    @Test
    @DisplayName("findByUsername cached")
    fun t3() {
        memberRepository.findByUsername("admin").getOrThrow() // 2번 회원 로드
        memberRepository.findByUsername("admin").getOrThrow() // 캐시
        memberRepository.findById(2).getOrThrow() // 캐시

        memberRepository.findById(1).getOrThrow() // 1번 회원 로드
        memberRepository.findByUsername("system").getOrThrow() // 캐시
    }
}