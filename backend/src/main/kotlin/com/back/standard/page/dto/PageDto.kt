package com.back.standard.page.dto

import org.springframework.data.domain.Page

class PageableDto(
    val currentPageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val offset: Long,
    val isSorted: Boolean
)

data class PageDto<T>(
    val content: List<T>,
    val pageable: PageableDto
) {
    constructor(page: Page<T>) : this(
        page.content,
        PageableDto(
            page.number + 1,
            page.size,
            page.totalPages,
            page.totalElements,
            page.numberOfElements,
            page.pageable.offset,
            page.pageable.sort.isSorted
        )
    )
}