package com.back.domain.post.post.controller

import com.back.domain.post.post.dto.PostDto
import com.back.domain.post.post.dto.PostWithContentDto
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.service.PostService
import com.back.domain.post.postUser.entity.PostUser
import com.back.global.rq.Rq
import com.back.global.rsData.RsData
import com.back.standard.extensions.getOrThrow
import com.back.standard.page.dto.PageDto
import com.back.standard.search.PostSearchKeywordType
import com.back.standard.search.PostSearchSortType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Page
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "ApiV1PostController", description = "API 글 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class ApiV1PostController(
    private val postService: PostService,
    private val rq: Rq
) {
    val actor: PostUser
        get() = PostUser(rq.actor)

    @GetMapping
    @Operation(summary = "다건 조회")
    @Transactional(readOnly = true)
    fun items(
        @RequestParam(defaultValue = "TITLE") keywordType: PostSearchKeywordType,
        @RequestParam(defaultValue = "") keyword: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "30") pageSize: Int,
        @RequestParam(defaultValue = "ID") sort: PostSearchSortType
    ): PageDto<PostDto> {
        val posts: Page<Post> = postService.findBySearchPaged(keywordType, keyword, page, pageSize, sort)

        return PageDto(posts.map { PostDto(it) })
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    fun item(@PathVariable id: Int): PostWithContentDto {
        val post = postService.findById(id).getOrThrow()

        return PostWithContentDto(post)
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    fun delete(
        @PathVariable id: Int
    ): RsData<Void> {
        val post = postService.findById(id).getOrThrow()

        post.checkActorCanDelete(actor)

        postService.delete(post)

        return RsData(
            "200-1",
            "${id}번 글이 삭제되었습니다."
        )
    }


    data class PostWriteReqBody(
        @field:NotBlank @field:Size(min = 2, max = 100) val title: String,
        @field:NotBlank @field:Size(min = 2, max = 5000) val content: String
    )

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    fun write(
        @RequestBody @Valid reqBody: PostWriteReqBody
    ): RsData<PostDto> {
        val post = postService.write(actor, reqBody.title, reqBody.content)

        return RsData(
            "201-1",
            "${post.id}번 글이 작성되었습니다.",
            PostDto(post)
        )
    }

    data class PostModifyReqBody(
        @field:NotBlank @field:Size(min = 2, max = 100) val title: String,
        @field:NotBlank @field:Size(min = 2, max = 5000) val content: String
    )

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    fun modify(
        @PathVariable id: Int,
        @RequestBody @Valid reqBody: PostModifyReqBody
    ): RsData<Void> {
        val post = postService.findById(id).getOrThrow()

        post.checkActorCanModify(actor)

        postService.modify(post, reqBody.title, reqBody.content)

        return RsData(
            "200-1",
            "${id}번 글이 수정되었습니다."
        )
    }
}