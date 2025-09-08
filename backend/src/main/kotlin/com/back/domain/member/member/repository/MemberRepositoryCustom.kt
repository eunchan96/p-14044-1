package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.Member
import com.back.standard.search.MemberSearchKeywordType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberRepositoryCustom {
    fun findByKeyword(
        keywordType: MemberSearchKeywordType,
        keyword: String,
        pageable: Pageable
    ): Page<Member>

    fun findByUsername(username: String): Member?

    // 실습
    fun findQById(id: Int): Member?
    fun findQByUsername(username: String): Member?
    fun findQByIdIn(ids: List<Int>): List<Member>
    fun findQByUsernameAndNickname(username: String, nickname: String): Member?
    fun findQByUsernameOrNickname(username: String, nickname: String): List<Member>

    fun findQByUsernameAndEitherPasswordOrNickname(username: String, password: String?, nickname: String?): List<Member>
    fun findQByNicknameContaining(nickname: String): List<Member>
    fun countQByNicknameContaining(nickname: String): Long
    fun existsQByNicknameContaining(nickname: String): Boolean

    fun findQByNicknameContaining(nickname: String, pageable: Pageable): Page<Member>
    fun findQByNicknameContainingOrderByIdDesc(nickname: String): List<Member>
    fun findQByUsernameContaining(username: String, pageable: Pageable): Page<Member>
}