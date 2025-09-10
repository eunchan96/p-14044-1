package com.back.domain.member.member.entity

//import com.back.global.app.AppConfig
import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.NaturalId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@MappedSuperclass
abstract class BaseMember(
    id: Int,
    @field:NaturalId @field:Column(unique = true) val username: String,
    var profileImgUrl: String?
) : BaseTime(id) {
    val profileImgUrlOrDefault: String
        get() {
            return profileImgUrl ?: "https://placehold.co/600x600?text=U_U"
        }

//    val redirectToProfileImgUrlOrDefault: String
//        get() = "${AppConfig.siteBackUrl}/api/v1/members/${id}/redirectToProfileImg"

    val isAdmin: Boolean
        get() = when (username) {
            "system", "admin" -> true
            else -> false
        }

    val authorities: List<GrantedAuthority>
        get() = this.authoritiesAsStringList
            .map { SimpleGrantedAuthority(it) }

    private val authoritiesAsStringList: List<String>
        get() {
            val authorities = mutableListOf<String>()

            if (isAdmin) authorities.add("ROLE_ADMIN")

            return authorities
        }
}
