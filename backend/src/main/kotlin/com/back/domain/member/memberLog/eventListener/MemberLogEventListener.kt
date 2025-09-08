package com.back.domain.member.memberLog.eventListener

import com.back.domain.member.memberLog.service.MemberLogService
import com.back.domain.post.postComment.event.PostCommentWrittenEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class MemberLogEventListener(
    private val memberLogService: MemberLogService
) {
    @EventListener
    fun handle(event: PostCommentWrittenEvent) {
        println("${event.actor.id}번 회원이 ${event.post.id}번 글에 ${event.postComment.id}번 댓글을 작성했습니다.")

        memberLogService.save(event)
    }
}