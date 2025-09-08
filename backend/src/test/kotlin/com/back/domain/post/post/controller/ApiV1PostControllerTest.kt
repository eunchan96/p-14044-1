package com.back.domain.post.post.controller

import com.back.domain.member.member.service.MemberService
import com.back.domain.post.post.service.PostService
import com.back.standard.extensions.getOrThrow
import com.back.standard.search.PostSearchKeywordType
import com.back.standard.search.PostSearchSortType
import jakarta.servlet.http.Cookie
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiV1PostControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var postService: PostService

    @Autowired
    private lateinit var memberService: MemberService

    @Test
    @DisplayName("글 쓰기")
    @WithUserDetails("user1")
    @Throws(Exception::class)
    fun t1() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "제목",
                                            "content": "내용"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        val post = postService.findLatest().getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("201-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${post.id}번 글이 작성되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(post.id))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.createDate")
                    .value(Matchers.startsWith(post.createDate.toString().take(20)))
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.modifyDate")
                    .value(Matchers.startsWith(post.modifyDate.toString().take(20)))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.authorId").value(post.author.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.authorName").value(post.author.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("제목"))
    }

    @Test
    @DisplayName("글 쓰기, with wrong apiKey, with valid accessToken")
    @Throws(Exception::class)
    fun t14() {
        val actor = memberService.findByUsername("user1").getOrThrow()
        val actorAccessToken = memberService.genAccessToken(actor)

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/posts")
                    .header("Authorization", "Bearer wrong-api-key $actorAccessToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "제목",
                                            "content": "내용"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    @DisplayName("글 쓰기, with wrong apiKey cookie, with valid accessToken cookie")
    @Throws(Exception::class)
    fun t15() {
        val actor = memberService.findByUsername("user1").getOrThrow()
        val actorAccessToken = memberService.genAccessToken(actor)

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/posts")
                    .cookie(
                        Cookie("apiKey", "wrong-api-key"),
                        Cookie("accessToken", actorAccessToken)
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "제목",
                                            "content": "내용"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    @DisplayName("글 쓰기, without title")
    @WithUserDetails("user1")
    @Throws(Exception::class)
    fun t7() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "",
                                            "content": "내용"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("400-1"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.msg").value(
                    """
                        title-NotBlank-must not be blank
                        title-Size-size must be between 2 and 100
                        """.trimIndent()
                )
            )
    }

    @Test
    @DisplayName("글 쓰기, without content")
    @WithUserDetails("user1")
    @Throws(Exception::class)
    fun t8() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "제목",
                                            "content": ""
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("400-1"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.msg").value(
                    """
                        content-NotBlank-must not be blank
                        content-Size-size must be between 2 and 5000
                        """.trimIndent()
                )
            )
    }

    @Test
    @DisplayName("글 쓰기, with wrong json syntax")
    @WithUserDetails("user1")
    @Throws(Exception::class)
    fun t9() {
        val wrongJsonBody = """
                {
                    "title": 제목",
                    content": "내용"
                
                """.trimIndent()

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(wrongJsonBody)
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("write"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("400-1"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.msg").value("요청 본문이 올바르지 않습니다.")
            )
    }

    @Test
    @DisplayName("글 쓰기, without authorization header")
    @Throws(Exception::class)
    fun t10() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "제목",
                                            "content": "내용"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("401-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("로그인 후 이용해주세요."))
    }

    @Test
    @DisplayName("글 쓰기, with wrong authorization header")
    @Throws(Exception::class)
    fun t11() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer wrong-api-key")
                    .content(
                        """
                                        {
                                            "title": "제목",
                                            "content": "내용"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("401-3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("API 키가 유효하지 않습니다."))
    }


    @Test
    @DisplayName("글 수정")
    @WithUserDetails("user1")
    @Throws(Exception::class)
    fun t2() {
        val id = 1

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.put("/api/v1/posts/${id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${id}번 글이 수정되었습니다."))
    }

    @Test
    @DisplayName("글 수정, without permission")
    @Throws(Exception::class)
    fun t12() {
        val id = 1

        val actor = memberService.findByUsername("user3").getOrThrow()
        val actorApiKey = actor.apiKey

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.put("/api/v1/posts/${id}")
                    .header("Authorization", "Bearer $actorApiKey")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        
                                        """.trimIndent()
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("403-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${id}번 글 수정권한이 없습니다."))
    }


    @Test
    @DisplayName("글 삭제")
    @WithUserDetails("user1")
    @Throws(Exception::class)
    fun t3() {
        val id = 1

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.delete("/api/v1/posts/${id}")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("delete"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${id}번 글이 삭제되었습니다."))
    }

    @Test
    @DisplayName("글 삭제, without permission")
    @Throws(Exception::class)
    fun t13() {
        val id = 1

        val actor = memberService.findByUsername("user3").getOrThrow()
        val actorApiKey = actor.apiKey

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.delete("/api/v1/posts/${id}")
                    .header("Authorization", "Bearer $actorApiKey")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("delete"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("403-2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${id}번 글 삭제권한이 없습니다."))
    }


    @Test
    @DisplayName("글 단건조회")
    @Throws(Exception::class)
    fun t4() {
        val id = 1

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/posts/${id}")
            )
            .andDo(MockMvcResultHandlers.print())

        val post = postService.findById(id).getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("item"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(post.id))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.createDate")
                    .value(Matchers.startsWith(post.createDate.toString().take(20)))
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.modifyDate")
                    .value(Matchers.startsWith(post.modifyDate.toString().take(20)))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value(post.author.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value(post.author.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(post.title))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(post.content))
    }

    @Test
    @DisplayName("글 단건조회, 404")
    @Throws(Exception::class)
    fun t6() {
        val id = Int.Companion.MAX_VALUE

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/posts/${id}")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("item"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("404-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."))
    }


    @Test
    @DisplayName("글 다건조회")
    @Throws(Exception::class)
    fun t5() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/posts")
            )
            .andDo(MockMvcResultHandlers.print())

        val postPage = postService.findBySearchPaged()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.currentPageNumber").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.pageSize").value(30))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalPages").value(postPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalElements").value(postPage.totalElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.numberOfElements").value(postPage.numberOfElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.offset").value(postPage.pageable.offset))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.isSorted").value(postPage.pageable.sort.isSorted))

        val posts = postPage.content
        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(posts.size))

        for (i in posts.indices) {
            val post = posts[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].id").value(post.id))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].createDate")
                        .value(Matchers.startsWith(post.createDate.toString().take(20)))
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].modifyDate")
                        .value(Matchers.startsWith(post.modifyDate.toString().take(20)))
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].authorId").value(post.author.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].authorName").value(post.author.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].title").value(post.title))
        }
    }

    @Test
    @DisplayName("다건 조회 with keyword=축구")
    fun t18() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/posts?page=1&pageSize=3&keyword=축구")
            )
            .andDo(MockMvcResultHandlers.print())

        val postPage = postService
            .findBySearchPaged(PostSearchKeywordType.TITLE, "축구", 1, 3)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.currentPageNumber").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.pageSize").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalPages").value(postPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalElements").value(postPage.totalElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.numberOfElements").value(postPage.numberOfElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.offset").value(postPage.pageable.offset))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.isSorted").value(postPage.pageable.sort.isSorted))

        val posts = postPage.content
        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(posts.size))

        for (i in posts.indices) {
            val post = posts[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].id").value(post.id))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].createDate")
                        .value(Matchers.startsWith(post.createDate.toString().take(20)))
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].modifyDate")
                        .value(Matchers.startsWith(post.modifyDate.toString().take(20)))
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].authorId").value(post.author.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].authorName").value(post.author.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].title").value(post.title))
        }
    }

    @Test
    @DisplayName("다건 조회 with keywordType=CONTENT&keyword=18명")
    fun t19() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/posts?page=1&pageSize=3&keywordType=CONTENT&keyword=18명")
            )
            .andDo(MockMvcResultHandlers.print())

        val postPage = postService
            .findBySearchPaged(PostSearchKeywordType.CONTENT, "18명", 1, 3)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.currentPageNumber").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.pageSize").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalPages").value(postPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalElements").value(postPage.totalElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.numberOfElements").value(postPage.numberOfElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.offset").value(postPage.pageable.offset))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.isSorted").value(postPage.pageable.sort.isSorted))

        val posts = postPage.content
        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(posts.size))

        for (i in posts.indices) {
            val post = posts[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].id").value(post.id))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].createDate")
                        .value(Matchers.startsWith(post.createDate.toString().take(20)))
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].modifyDate")
                        .value(Matchers.startsWith(post.modifyDate.toString().take(20)))
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].authorId").value(post.author.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].authorName").value(post.author.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].title").value(post.title))
        }
    }

    @Test
    @DisplayName("글 다건조회 - with keyword=발야구&sort=AUTHOR_NAME_ASC")
    @Throws(Exception::class)
    fun t20() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/posts?page=1&pageSize=30&keywordType=TITLE&keyword=발야구&sort=AUTHOR_NAME_ASC")
            )
            .andDo(MockMvcResultHandlers.print())

        val postPage = postService.findBySearchPaged(PostSearchKeywordType.TITLE, "발야구", 1, 30, PostSearchSortType.AUTHOR_NAME_ASC)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiV1PostController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.currentPageNumber").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.pageSize").value(30))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalPages").value(postPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.totalElements").value(postPage.totalElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.numberOfElements").value(postPage.numberOfElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.offset").value(postPage.pageable.offset))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.isSorted").value(postPage.pageable.sort.isSorted))

        val posts = postPage.content
        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(posts.size))

        for (i in posts.indices) {
            val post = posts[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].id").value(post.id))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].createDate")
                        .value(Matchers.startsWith(post.createDate.toString().take(20)))
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[$i].modifyDate")
                        .value(Matchers.startsWith(post.modifyDate.toString().take(20)))
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].authorId").value(post.author.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].authorName").value(post.author.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[$i].title").value(post.title))
        }
    }
}
