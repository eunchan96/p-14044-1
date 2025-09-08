package com.back.domain.member.member.controller

import com.back.domain.member.member.service.MemberService
import com.back.standard.extensions.getOrThrow
import com.back.standard.search.MemberSearchKeywordType
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiV1AdmMemberControllerTest {
    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    @DisplayName("다건조회")
    @WithUserDetails("admin")
    @Throws(Exception::class)
    fun t1() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/adm/members")
            )
            .andDo(MockMvcResultHandlers.print())

        val memberPage = memberService.findBySearchPaged()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1AdmMemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.currentPageNumber").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.pageSize").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalPages").value(memberPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalElements").value(memberPage.totalElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.numberOfElements").value(memberPage.numberOfElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.offset").value(memberPage.pageable.offset))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.isSorted").value(memberPage.pageable.sort.isSorted))

        val members = memberPage.content
        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(members.size))

        for (i in members.indices) {
            val member = members[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].id").value(member.id))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].createDate")
                        .value(Matchers.startsWith(member.createDate.toString().take(20)))
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].modifyDate")
                        .value(Matchers.startsWith(member.modifyDate.toString().take(20)))
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].name").value(member.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].username").value(member.username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].isAdmin").value(member.isAdmin))
        }
    }

    @Test
    @DisplayName("다건조회, without permission")
    @WithUserDetails("user1")
    @Throws(Exception::class)
    fun t3() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/adm/members")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("403-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("권한이 없습니다."))
    }

    @Test
    @DisplayName("단건조회")
    @WithUserDetails("admin")
    @Throws(Exception::class)
    fun t2() {
        val id = 1

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/adm/members/$id")
            )
            .andDo(MockMvcResultHandlers.print())

        val member = memberService.findById(id).getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1AdmMemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("item"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(member.id))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.createDate")
                    .value(Matchers.startsWith(member.createDate.toString().take(20)))
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.modifyDate")
                    .value(Matchers.startsWith(member.modifyDate.toString().take(20)))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(member.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(member.username))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isAdmin").value(member.isAdmin))
    }

    @Test
    @DisplayName("단건조회, without permission")
    @WithUserDetails("user1")
    @Throws(Exception::class)
    fun t4() {
        val id = 1

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/adm/members/$id")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("403-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("권한이 없습니다."))
    }

    @Test
    @DisplayName("다건 조회 with searchKeyword=user")
    @WithUserDetails("admin")
    fun t19() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/adm/members?page=1&pageSize=3&keyword=user")
            )
            .andDo(MockMvcResultHandlers.print())

        val memberPage = memberService.findBySearchPaged(MemberSearchKeywordType.USERNAME, "user", 1, 3)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1AdmMemberController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.currentPageNumber").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.pageSize").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalPages").value(memberPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalElements").value(memberPage.totalElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.numberOfElements").value(memberPage.numberOfElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.offset").value(memberPage.pageable.offset))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.isSorted").value(memberPage.pageable.sort.isSorted))

        val members = memberPage.content
        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(members.size))

        for (i in members.indices) {
            val member = members[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].id").value(member.id))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].createDate")
                        .value(Matchers.startsWith(member.createDate.toString().take(20)))
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].modifyDate")
                        .value(Matchers.startsWith(member.modifyDate.toString().take(20)))
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].name").value(member.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].username").value(member.username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].isAdmin").value(member.isAdmin))
        }
    }
}
