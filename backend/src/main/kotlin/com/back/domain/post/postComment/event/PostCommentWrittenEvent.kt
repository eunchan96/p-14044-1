package com.back.domain.post.postComment.event

import com.back.domain.post.post.dto.PostDto
import com.back.domain.post.postComment.dto.PostCommentDto
import com.back.domain.post.postUser.dto.PostUserDto
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class PostCommentWrittenEvent(
    @field:JsonIgnore val actor: PostUserDto,
    @field:JsonIgnore val owner: PostUserDto,
    @field:JsonIgnoreProperties("title", "content") val post: PostDto,
    @field:JsonIgnoreProperties("content") val postComment: PostCommentDto
)