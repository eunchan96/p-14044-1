package com.back.domain.post.post.repository

import com.back.domain.post.post.entity.Post
import com.back.standard.search.PostSearchKeywordType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostRepositoryCustom {
    fun findByKeyword(
        keywordType: PostSearchKeywordType,
        keyword: String,
        pageable: Pageable
    ): Page<Post>
}