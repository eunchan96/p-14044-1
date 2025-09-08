package com.back.domain.member.member.repository

import com.back.standard.extensions.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberRepositoryBasicTest {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    @DisplayName("findById")
    fun t1() {
        val member = memberRepository.findById(1).get()

        assertThat(member.id).isEqualTo(1)
    }

    @Test
    @DisplayName("findQById")
    fun t1_1() {
        val member = memberRepository.findQById(1).getOrThrow()

        assertThat(member.id).isEqualTo(1)
    }

    @Test
    @DisplayName("findByUsername")
    fun t2() {
        val member = memberRepository.findByUsername("admin").getOrThrow()

        assertThat(member.username).isEqualTo("admin")
    }

    @Test
    @DisplayName("findQByUsername")
    fun t2_1() {
        val member = memberRepository.findQByUsername("admin").getOrThrow()

        assertThat(member.username).isEqualTo("admin")
    }

    @Test
    @DisplayName("findByIdIn")
    fun t3() {
        val members = memberRepository.findByIdIn(listOf(1, 2, 3))

        assertThat(members).isNotEmpty
        assertThat(members.map { it.id }).containsAnyOf(1, 2, 3)
    }

    @Test
    @DisplayName("findQByIdIn")
    fun t3_1() {
        val members = memberRepository.findQByIdIn(listOf(1, 2, 3))

        assertThat(members).isNotEmpty
        assertThat(members.map { it.id }).containsAnyOf(1, 2, 3)
    }

    @Test
    @DisplayName("findByUsernameAndNickname")
    fun t4() {
        val member = memberRepository.findByUsernameAndNickname("admin", "관리자").getOrThrow()

        assertThat(member.username).isEqualTo("admin")
        assertThat(member.nickname).isEqualTo("관리자")
    }

    @Test
    @DisplayName("findQByUsernameAndNickname")
    fun t4_1() {
        val member = memberRepository.findQByUsernameAndNickname("admin", "관리자").getOrThrow()

        assertThat(member.username).isEqualTo("admin")
        assertThat(member.nickname).isEqualTo("관리자")
    }

    @Test
    @DisplayName("findByUsernameOrNickname")
    fun t5() {
        val members = memberRepository.findByUsernameOrNickname("admin", "유저1")

        assertThat(members).isNotEmpty
        assertThat(members.any { it.username == "admin" || it.nickname == "유저1" }).isTrue
    }

    @Test
    @DisplayName("findQByUsernameOrNickname")
    fun t5_1() {
        val members = memberRepository.findQByUsernameOrNickname("admin", "유저1")

        assertThat(members).isNotEmpty
        assertThat(members.any { it.username == "admin" || it.nickname == "유저1" }).isTrue
    }

    @Test
    @DisplayName("findCByUsernameAndEitherPasswordOrNickname")
    fun t6() {
        val members = memberRepository.findCByUsernameAndEitherPasswordOrNickname("admin", "wrong-password", "관리자")

        assertThat(members).isNotEmpty
        assertThat(members.any { it.username == "admin" && (it.password == "wrong-password" || it.nickname == "관리자") }).isTrue
    }

    @Test
    @DisplayName("findQByUsernameAndPasswordOrNickname")
    fun t6_1() {
        val members = memberRepository.findQByUsernameAndEitherPasswordOrNickname("admin", "wrong-password", "관리자")

        assertThat(members).isNotEmpty
        assertThat(members.any { it.username == "admin" && (it.password == "wrong-password" || it.nickname == "관리자") }).isTrue
    }

    @Test
    @DisplayName("findByNicknameContaining")
    fun t7() {
        val members = memberRepository.findByNicknameContaining("유저")

        assertThat(members).isNotEmpty
        assertThat(members.all { it.nickname.contains("유저") }).isTrue
    }

    @Test
    @DisplayName("findQByNicknameContaining")
    fun t7_1() {
        val members = memberRepository.findQByNicknameContaining("유저")

        assertThat(members).isNotEmpty
        assertThat(members.all { it.nickname.contains("유저") }).isTrue
    }

    @Test
    @DisplayName("countByNicknameContaining")
    fun t8() {
        val count = memberRepository.countByNicknameContaining("유저")

        assertThat(count).isEqualTo(6)
    }

    @Test
    @DisplayName("countQByNicknameContaining")
    fun t8_1() {
        val count = memberRepository.countQByNicknameContaining("유저")

        assertThat(count).isEqualTo(6)
    }

    @Test
    @DisplayName("existsByNicknameContaining")
    fun t9() {
        val exists = memberRepository.existsByNicknameContaining("유저")

        assertThat(exists).isTrue
    }

    @Test
    @DisplayName("existsQByNicknameContaining")
    fun t9_1() {
        val exists = memberRepository.existsQByNicknameContaining("유저")

        assertThat(exists).isTrue
    }

    @Test
    @DisplayName("findByNicknameContaining with Pageable")
    fun t10() {
        val pageable = PageRequest.of(0, 2)
        val page = memberRepository.findByNicknameContaining("유저", pageable)

        assertThat(page.content).hasSize(2)
        assertThat(page.totalElements).isEqualTo(6)
        assertThat(page.totalPages).isEqualTo(3)  // 6 / 2
    }

    @Test
    @DisplayName("findQByNicknameContaining with Pageable")
    fun t10_1() {
        val pageable = PageRequest.of(0, 2)
        val page = memberRepository.findQByNicknameContaining("유저", pageable)

        assertThat(page.content).hasSize(2)
        assertThat(page.totalElements).isEqualTo(6)
        assertThat(page.totalPages).isEqualTo(3)  // 6 / 2
    }

    @Test
    @DisplayName("findByNicknameContainingOrderByIdDesc")
    fun t11() {
        val members = memberRepository.findByNicknameContainingOrderByIdDesc("유저")

        assertThat(members).isNotEmpty
        assertThat(members.all { it.nickname.contains("유저") }).isTrue

        for (i in 0 until members.size - 1) {
            assertThat(members[i].id).isGreaterThan(members[i + 1].id)
        }
    }

    @Test
    @DisplayName("findQByNicknameContainingOrderByIdDesc")
    fun t11_1() {
        val members = memberRepository.findQByNicknameContainingOrderByIdDesc("유저")

        assertThat(members).isNotEmpty
        assertThat(members.all { it.nickname.contains("유저") }).isTrue

        for (i in 0 until members.size - 1) {
            assertThat(members[i].id).isGreaterThan(members[i + 1].id)
        }
    }

    @Test
    @DisplayName("findByUsernameContaining with Pageable")
    fun t12() {
        val pageable = PageRequest.of(0, 2, Sort.by("id").descending())
        val page = memberRepository.findByUsernameContaining("user", pageable)

        for (i in 0 until page.content.size - 1) {
            assertThat(page.content[i].id).isGreaterThan(page.content[i + 1].id)
        }
    }

    @Test
    @DisplayName("findQByUsernameContaining with Pageable")
    fun t12_1() {
        val pageable = PageRequest.of(
            0, 2,
            Sort.by("id").descending()
                .and(Sort.by("username").ascending())
                .and(Sort.by("nickname").descending())
        )
        val page = memberRepository.findQByUsernameContaining("user", pageable)

        for (i in 0 until page.content.size - 1) {
            assertThat(page.content[i].id).isGreaterThan(page.content[i + 1].id)
        }
    }
}