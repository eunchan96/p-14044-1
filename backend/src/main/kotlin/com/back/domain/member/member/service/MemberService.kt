package com.back.domain.member.member.service

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.repository.MemberRepository
import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import com.back.standard.search.MemberSearchKeywordType
import com.back.standard.search.MemberSearchKeywordType.USERNAME
import com.back.standard.search.MemberSearchSortType
import com.back.standard.search.MemberSearchSortType.ID
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val authTokenService: AuthTokenService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder

) {
    fun count(): Long = memberRepository.count()

    fun join(username: String, password: String?, nickname: String, profileImgUrl: String? = null): Member {
        memberRepository
            .findByUsername(username)?.let {
                throw ServiceException("409-1", "이미 존재하는 아이디입니다.")
            }

        val member = Member(
            username,
            password?.takeIf { it.isNotBlank() }?.let { passwordEncoder.encode(it) },
            nickname,
            profileImgUrl
        )

        return memberRepository.save<Member>(member)
    }

    fun findByUsername(username: String): Member? = memberRepository.findByUsername(username)

    fun findByApiKey(apiKey: String): Member? = memberRepository.findByApiKey(apiKey)

    fun genAccessToken(member: Member): String = authTokenService.genAccessToken(member)

    fun payload(accessToken: String): Map<String, Any>? = authTokenService.payload(accessToken)

    fun findById(id: Int): Member? = memberRepository.findById(id).orElse(null)

    fun findAll(): List<Member> = memberRepository.findAll()

    fun checkPassword(member: Member, password: String) {
        val hashedPassword = member.password
        if (!passwordEncoder.matches(password, hashedPassword))
            throw ServiceException(
                "401-1",
                "비밀번호가 일치하지 않습니다."
            )
    }

    fun modifyOrJoin(username: String, password: String, nickname: String, profileImgUrl: String): RsData<Member> =
        findByUsername(username)?.let {
            modify(it, nickname, profileImgUrl)
            RsData("200-1", "회원 정보가 수정되었습니다.", it)
        } ?: run {
            val joined = join(username, password, nickname, profileImgUrl)
            RsData("201-1", "회원가입이 완료되었습니다.", joined)
        }

    private fun modify(member: Member, nickname: String, profileImgUrl: String) = member.modify(nickname, profileImgUrl)

    fun findBySearchPaged(
        keywordType: MemberSearchKeywordType = USERNAME,
        keyword: String = "",
        page: Int = 1,
        pageSize: Int = 10,
        sort: MemberSearchSortType = ID
    ): Page<Member> {
        val pageSize = if (pageSize in 1..50) pageSize else 10
        val page = if (page > 0) page else 1
        val pageable = PageRequest.of(page - 1, pageSize, sort.sortBy)
        return memberRepository.findByKeyword(keywordType, keyword, pageable)
    }
}