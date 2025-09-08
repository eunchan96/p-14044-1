package com.back.domain.post.postUser.entity

import com.back.domain.member.member.entity.BaseMember
import com.back.domain.member.member.entity.Member
import com.back.domain.post.postUser.service.PostUserAttrService
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.springframework.data.annotation.Immutable

// Member.kt : 회원 도메인 전용 객체
// PostUser.kt : 게시글 도메인 전용 객체
// MEMBER 테이블을 같이 공유한다

// 분리 장점 : 글과 관련된 필드는 PostUser 에만 넣어주면 되고, Post 도메인에는 PostUser 엔티티 사용, MSA 구현 시 유용
// DDD 에서 하나의 테이블 => N개의 엔티티 클래스
// MSA 에서는 자연스럽게 각 MicroService 에서 필요한 엔티티만 사용
@Entity
@Immutable
@Table(name = "MEMBER")
class PostUser(
    id: Int,
    username: String,
    @field:Column(name = "NICKNAME") var name: String, // NICKNAME 필드를 name 이라는 다른 필드명으로 사용 가능
    profileImgUrl: String? = null,
) : BaseMember(id, username, profileImgUrl) {
    constructor(member: Member) : this(
        member.id,
        member.username,
        member.name,
        member.profileImgUrl
    )

    companion object {
        lateinit var attrService: PostUserAttrService
    }

    val postsCount: Int
        get() = attrService.findBySubjectAndName(this, "postsCount")?.value?.toInt() ?: 0

    val postCommentsCount: Int
        get() = attrService.findBySubjectAndName(this, "postCommentsCount")?.value?.toInt() ?: 0

    fun incrementPostsCount() {
        attrService.incrementPostsCount(this)
    }

    fun decrementPostsCount() {
        attrService.decrementPostsCount(this)
    }

    fun incrementPostCommentsCount() {
        attrService.incrementPostCommentsCount(this)
    }

    fun decrementPostCommentsCount() {
        attrService.decrementPostCommentsCount(this)
    }
}