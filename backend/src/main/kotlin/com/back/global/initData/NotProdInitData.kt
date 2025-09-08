package com.back.global.initData

import com.back.domain.member.member.service.MemberService
import com.back.domain.post.post.service.PostService
import com.back.domain.post.postUser.service.PostUserService
import com.back.global.app.CustomConfigProperties
import com.back.standard.extensions.getOrThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional

@Profile("!prod")
@Configuration
class NotProdInitData(
    private val memberService: MemberService,
    private val postService: PostService,
    private val postUserService: PostUserService,
    private val customConfigProperties: CustomConfigProperties
) {
    @Autowired
    @Lazy
    private lateinit var self: NotProdInitData

    @Bean
    fun notProdInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments? ->
            self.work1()
//            self.work2()
            self.work3()
        }
    }

    @Transactional
    fun work1() {
        if (memberService.count() > 0) return

        val memberSystem = memberService.join("system", "1234", "시스템")
        val memberAdmin = memberService.join("admin", "1234", "관리자")
        val memberUser1 = memberService.join("user1", "1234", "유저1")
        val memberUser2 = memberService.join("user2", "1234", "유저2")
        val memberUser3 = memberService.join("user3", "1234", "유저3")
        val memberUser4 = memberService.join("user4", "1234", "유저4")
        val memberUser5 = memberService.join("user5", "1234", "유저5")
        val memberUser6 = memberService.join("user6", "1234", "유저6")

        memberSystem.modifyApiKey(memberSystem.username)
        memberAdmin.modifyApiKey(memberAdmin.username)
        memberUser1.modifyApiKey(memberUser1.username)
        memberUser2.modifyApiKey(memberUser2.username)
        memberUser3.modifyApiKey(memberUser3.username)
        memberUser4.modifyApiKey(memberUser4.username)
        memberUser5.modifyApiKey(memberUser5.username)
        memberUser6.modifyApiKey(memberUser6.username)

        customConfigProperties.notProdMembers.forEach { notProdMember ->
            val socialMember = memberService.join(
                notProdMember.username,
                null,
                notProdMember.nickname,
                notProdMember.profileImgUrl
            )
            socialMember.modifyApiKey(notProdMember.apiKey)
        }
    }

    @Transactional
    fun work2() {
        if (postService.count() > 0) return

        val memberUser1 = postUserService.findByUsername("user1").getOrThrow()
        val memberUser2 = postUserService.findByUsername("user2").getOrThrow()
        val memberUser3 = postUserService.findByUsername("user3").getOrThrow()

        val post1 = postService.write(memberUser1, "제목 1", "내용 1")
        val post2 = postService.write(memberUser1, "제목 2", "내용 2")
        val post3 = postService.write(memberUser2, "제목 3", "내용 3")

        postService.writeComment(memberUser1, post1, "댓글 1-1")
        postService.writeComment(memberUser1, post1, "댓글 1-2")
        postService.writeComment(memberUser2, post1, "댓글 1-3")
        postService.writeComment(memberUser3, post2, "댓글 2-1")
        postService.writeComment(memberUser3, post2, "댓글 2-2")
    }

    @Transactional
    fun work3() {
        if (postService.count() > 0) return

        val memberUser1 = postUserService.findByUsername("user1").getOrThrow()
        val memberUser2 = postUserService.findByUsername("user2").getOrThrow()
        val memberUser3 = postUserService.findByUsername("user3").getOrThrow()
        val memberUser4 = postUserService.findByUsername("user4").getOrThrow()
        val memberUser5 = postUserService.findByUsername("user5").getOrThrow()
        val memberUser6 = postUserService.findByUsername("user6").getOrThrow()

        val post1 = postService.write(
            memberUser1,
            "축구 하실 분?",
            "14시 까지 22명을 모아야 합니다."
        )
        postService.writeComment(memberUser1, post1, "대답.")
        postService.writeComment(memberUser2, post1, "저요!")
        postService.writeComment(memberUser3, post1, "저도 할래요.")

        val post2 = postService.write(
            memberUser1,
            "배구 하실 분?",
            "15시 까지 12명을 모아야 합니다."
        )
        postService.writeComment(memberUser4, post2, "저요!, 저 배구 잘합니다.")

        val post3 = postService.write(
            memberUser2,
            "농구 하실 분?",
            "16시 까지 10명을 모아야 합니다."
        )

        val post4 = postService.write(
            memberUser3,
            "발야구 하실 분?",
            "17시 까지 14명을 모아야 합니다."
        )

        val post5 = postService.write(
            memberUser4,
            "피구 하실 분?",
            "18시 까지 18명을 모아야 합니다."
        )

        val post6 = postService.write(
            memberUser4,
            "발야구를 밤에 하실 분?",
            "22시 까지 18명을 모아야 합니다."
        )

        val post7 = postService.write(
            memberUser4,
            "발야구를 새벽 1시에 하실 분?",
            "새벽 1시 까지 17명을 모아야 합니다."
        )

        val post8 = postService.write(
            memberUser4,
            "발야구를 새벽 3시에 하실 분?",
            "새벽 3시 까지 19명을 모아야 합니다."
        )

        val post9 = postService.write(
            memberUser4,
            "테이블테니스를 하실 분있나요?",
            "테이블테니스 강력 추천합니다."
        )
        val post10 = postService.write(
            memberUser4,
            "테니스 하실 분있나요?",
            "테니스 강력 추천합니다."
        )

        (11..100).forEach { i: Int ->
            postService.write(
                memberUser5,
                "테스트 게시물 $i",
                "테스트 게시물 $i 내용"
            )
        }

        (101..200).forEach { i: Int ->
            postService.write(
                memberUser6,
                "테스트 게시물 $i",
                "테스트 게시물 $i 내용"
            )
        }
    }
}