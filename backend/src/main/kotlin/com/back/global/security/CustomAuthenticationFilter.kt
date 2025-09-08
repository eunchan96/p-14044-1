package com.back.global.security

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.service.MemberService
import com.back.global.exception.ServiceException
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import com.back.standard.util.Ut.json.toString
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CustomAuthenticationFilter(
    private val memberService: MemberService,
    private val rq: Rq
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            processRequest(request, response, filterChain)
        } catch (e: ServiceException) {
            val rsData: RsData<Void> = e.rsData
            response.contentType = "application/json; charset=utf-8"
            response.status = rsData.statusCode
            response.writer.write(
                toString(rsData)
            )
        }
    }

    private fun processRequest(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // API 요청이 아니라면 패스
        if (!request.requestURI.startsWith("/api/")) {
            filterChain.doFilter(request, response)
            return
        }

        // 인증, 인가가 필요없는 API 요청이라면 패스
        if (request.requestURI in listOf("/api/v1/members/login", "/api/v1/members/logout", "/api/v1/members/join")) {
            filterChain.doFilter(request, response)
            return
        }

        val (apiKey, accessToken) = extractTokens()
        logger.debug("apiKey : $apiKey")
        logger.debug("accessToken : $accessToken")

        val isApiKeyExists = apiKey.isNotBlank()
        val isAccessTokenExists = accessToken.isNotBlank()

        if (!isApiKeyExists && !isAccessTokenExists) {
            filterChain.doFilter(request, response)
            return
        }

        val (member, isAccessTokenValid) = getAuthenticatedMember(apiKey, accessToken)

        if (isAccessTokenExists && !isAccessTokenValid) {
            val actorAccessToken = memberService.genAccessToken(member)

            rq.setCookie("accessToken", actorAccessToken)
            rq.setHeader("Authorization", actorAccessToken)
        }

        val user: UserDetails = SecurityUser(
            member.id,
            member.username,
            "",
            member.name,
            member.authorities
        )

        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            user,
            user.password,
            user.authorities
        )

        // 이 시점 이후부터는 시큐리티가 이 요청을 인증된 사용자의 요청이다.
        SecurityContextHolder
            .getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }

    private fun extractTokens(): Pair<String, String> {
        val headerAuthorization = rq.getHeader("Authorization", "")

        return if (headerAuthorization.isNotBlank()) {
            require(headerAuthorization.startsWith("Bearer ")) {
                throw ServiceException("401-2", "Authorization 헤더가 Bearer 형식이 아닙니다.")
            }

            val headerAuthorBits = headerAuthorization.split(' ', limit = 3)
            val apiKey = headerAuthorBits[1]
            val accessToken = headerAuthorBits.getOrElse(2) { "" }
            apiKey to accessToken
        } else {
            val apiKey = rq.getCookieValue("apiKey", "")
            val accessToken = rq.getCookieValue("accessToken", "")
            apiKey to accessToken
        }
    }

    private fun getAuthenticatedMember(apiKey: String, accessToken: String): Pair<Member, Boolean> {
        var isAccessTokenValid = false

        val memberFromToken = if (accessToken.isNotBlank()) {
            memberService.payload(accessToken)?.let { payload ->
                val id = payload["id"] as Int
                val username = payload["username"] as String
                val name = payload["name"] as String
                Member(id, username, name).also { isAccessTokenValid = true }
            }
        } else null

        val member = memberFromToken ?: memberService.findByApiKey(apiKey)
            ?: throw ServiceException("401-3", "API 키가 유효하지 않습니다.")

        return member to isAccessTokenValid
    }
}
