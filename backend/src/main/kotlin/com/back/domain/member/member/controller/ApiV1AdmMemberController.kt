package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberWithUsernameDto
import com.back.domain.member.member.service.MemberService
import com.back.standard.extensions.getOrThrow
import com.back.standard.page.dto.PageDto
import com.back.standard.search.MemberSearchKeywordType
import com.back.standard.search.MemberSearchSortType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/adm/members")
@Tag(name = "ApiV1AdmMemberController", description = "관리자용 API 회원 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class ApiV1AdmMemberController(
    private val memberService: MemberService
) {
    @Operation(summary = "다건 조회")
    @Transactional(readOnly = true)
    @GetMapping
    fun items(
        @RequestParam(defaultValue = "ALL") keywordType: MemberSearchKeywordType,
        @RequestParam(defaultValue = "") keyword: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(defaultValue = "ID") sort: MemberSearchSortType
    ): PageDto<MemberWithUsernameDto> {
        val members = memberService.findBySearchPaged(keywordType, keyword, page, pageSize, sort)

        return PageDto(members.map { MemberWithUsernameDto(it) })
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    fun item(
        @PathVariable id: Int
    ): MemberWithUsernameDto {
        val member = memberService.findById(id).getOrThrow()

        return MemberWithUsernameDto(member)
    }
}
